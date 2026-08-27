package com.yilian.blurdemo

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import eightbitlab.com.blurview.BlurTarget
import eightbitlab.com.blurview.BlurView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        convertSize(findViewById(R.id.i1), R.mipmap.i1)
        convertSize(findViewById(R.id.i2), R.mipmap.i2)
        convertSize(findViewById(R.id.i3), R.mipmap.i3)
        convertSize(findViewById(R.id.i4), R.mipmap.i4)
        convertSize(findViewById(R.id.i5), R.mipmap.i5)

        setupBlurView()
    }


    private fun convertSize(view: ImageView, res: Int) {
        val drawable = ContextCompat.getDrawable(this, res)!!
        val width = resources.displayMetrics.widthPixels

        // 原始图片宽高（比如Drawable、Bitmap的宽高）
        val originW = drawable.intrinsicWidth
        val originH = drawable.intrinsicHeight

        // 宽度固定为屏幕宽度，计算等比高度
        val targetHeight = (originH * width.toFloat() / originW).toInt()
        // 设置ImageView尺寸
        view.layoutParams = view.layoutParams.apply {
            height = targetHeight
        }
    }

    private fun setupBlurView() {
        val radius = 25f
        val target = findViewById<BlurTarget>(R.id.target)
        val bv = findViewById<BlurView>(R.id.bv)
        //set background, if your root layout doesn't have one
        val windowBackground = window.decorView.background
        bv.setupWith(target)
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(radius)
    }
}