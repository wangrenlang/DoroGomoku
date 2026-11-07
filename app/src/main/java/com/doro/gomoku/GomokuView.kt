package com.doro.gomoku

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

/**
 * 最简五子棋棋盘：
 * - 15 x 15 格
 * - 点击落子，黑白轮流
 * - 任意方向连成 5 子就算赢
 */
class GomokuView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    companion object {
        const val SIZE = 15          // 15 路棋盘
        const val EMPTY = 0
        const val BLACK = 1
        const val WHITE = 2
        const val WIN_COUNT = 5
    }

    /** 0=空 1=黑 2=白 */
    private val board = Array(SIZE) { IntArray(SIZE) }

    private var isBlackTurn = true
    private var gameOver = false

    /** 回调：分出胜负时通知外面 */
    var onGameOver: ((Int) -> Unit)? = null

    /** 回调：轮到谁走 */
    var onTurnChanged: ((Boolean) -> Unit)? = null

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#8A6A5A")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val blackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#2B2B2B")
        style = Paint.Style.FILL
    }

    private val whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val whiteEdgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#B0B0B0")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val lastMarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF6B8A")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F6DFC4")
        style = Paint.Style.FILL
    }

    // 格子边长、棋盘左上角偏移
    private var cell = 0f
    private var offsetX = 0f
    private var offsetY = 0f

    private var lastRow = -1
    private var lastCol = -1

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val side = min(w, h).toFloat()
        // 留一点边距给最外圈棋子
        cell = side / (SIZE + 1)
        offsetX = (w - cell * (SIZE - 1)) / 2f
        offsetY = (h - cell * (SIZE - 1)) / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 棋盘底色
        canvas.drawColor(Color.parseColor("#FFF4F7"))
        canvas.drawRect(
            offsetX - cell, offsetY - cell,
            offsetX + cell * (SIZE - 1) + cell,
            offsetY + cell * (SIZE - 1) + cell,
            bgPaint
        )

        // 网格线
        for (i in 0 until SIZE) {
            canvas.drawLine(
                offsetX, offsetY + i * cell,
                offsetX + cell * (SIZE - 1), offsetY + i * cell,
                gridPaint
            )
            canvas.drawLine(
                offsetX + i * cell, offsetY,
                offsetX + i * cell, offsetY + cell * (SIZE - 1),
                gridPaint
            )
        }

        // 棋子
        val r = cell * 0.42f
        for (row in 0 until SIZE) {
            for (col in 0 until SIZE) {
                val v = board[row][col]
                if (v == EMPTY) continue
                val cx = offsetX + col * cell
                val cy = offsetY + row * cell
                if (v == BLACK) {
                    canvas.drawCircle(cx, cy, r, blackPaint)
                } else {
                    canvas.drawCircle(cx, cy, r, whitePaint)
                    canvas.drawCircle(cx, cy, r, whiteEdgePaint)
                }
            }
        }

        // 标记最后一手
        if (lastRow >= 0 && lastCol >= 0) {
            val cx = offsetX + lastCol * cell
            val cy = offsetY + lastRow * cell
            canvas.drawCircle(cx, cy, r * 0.35f, lastMarkPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) return true
        if (gameOver) return true

        val col = Math.round((event.x - offsetX) / cell)
        val row = Math.round((event.y - offsetY) / cell)

        // 点到棋盘外面
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return true
        // 已经有子了
        if (board[row][col] != EMPTY) return true

        board[row][col] = if (isBlackTurn) BLACK else WHITE
        lastRow = row
        lastCol = col

        val winner = board[row][col]
        if (checkWin(row, col, winner)) {
            gameOver = true
            invalidate()
            onGameOver?.invoke(winner)
            return true
        }

        if (isBoardFull()) {
            gameOver = true
            invalidate()
            onGameOver?.invoke(EMPTY)
            return true
        }

        isBlackTurn = !isBlackTurn
        onTurnChanged?.invoke(isBlackTurn)
        invalidate()
        return true
    }

    /** 从刚落下的一点出发，看四个方向有没有连成五子 */
    private fun checkWin(row: Int, col: Int, who: Int): Boolean {
        val dirs = arrayOf(
            intArrayOf(0, 1),   // 横
            intArrayOf(1, 0),   // 竖
            intArrayOf(1, 1),   // 斜 ↘
            intArrayOf(1, -1)   // 斜 ↙
        )
        for (d in dirs) {
            var count = 1
            // 正方向
            var r = row + d[0]
            var c = col + d[1]
            while (r in 0 until SIZE && c in 0 until SIZE && board[r][c] == who) {
                count++
                r += d[0]
                c += d[1]
            }
            // 反方向
            r = row - d[0]
            c = col - d[1]
            while (r in 0 until SIZE && c in 0 until SIZE && board[r][c] == who) {
                count++
                r -= d[0]
                c -= d[1]
            }
            if (count >= WIN_COUNT) return true
        }
        return false
    }

    private fun isBoardFull(): Boolean {
        for (row in 0 until SIZE) {
            for (col in 0 until SIZE) {
                if (board[row][col] == EMPTY) return false
            }
        }
        return true
    }

    /** 清空棋盘，重新开始 */
    fun reset() {
        for (row in 0 until SIZE) {
            for (col in 0 until SIZE) {
                board[row][col] = EMPTY
            }
        }
        isBlackTurn = true
        gameOver = false
        lastRow = -1
        lastCol = -1
        invalidate()
    }
}
