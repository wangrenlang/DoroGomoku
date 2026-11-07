package com.doro.gomoku

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

/**
 * 主界面：上面一行提示文字，中间棋盘，下面一个「重新开始」按钮。
 * 全部用代码搭界面，省得写一堆 xml。
 */
class MainActivity : Activity() {

    private lateinit var boardView: GomokuView
    private lateinit var tipText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#FFF4F7"))
            setPadding(24, 48, 24, 32)
        }

        tipText = TextView(this).apply {
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#7A4A5A"))
            text = "黑棋先走，落子吧～"
        }
        root.addView(
            tipText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 24 }
        )

        boardView = GomokuView(this)
        boardView.onGameOver = { winner ->
            tipText.text = when (winner) {
                GomokuView.BLACK -> "黑棋赢啦！(◕‿◕)"
                GomokuView.WHITE -> "白棋赢啦！(◕‿◕)"
                else -> "平局～棋盘满啦"
            }
        }
        boardView.onTurnChanged = { isBlack ->
            tipText.text = if (isBlack) "轮到黑棋了" else "轮到白棋了"
        }
        root.addView(
            boardView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val resetBtn = Button(this).apply {
            text = "重新开始"
            setOnClickListener {
                boardView.reset()
                tipText.text = "黑棋先走，落子吧～"
            }
        }
        root.addView(
            resetBtn,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 16 }
        )

        setContentView(root)
    }
}
