package com.example.musicplayer

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.BuildCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        setContentView(R.layout.activity_settings)
        supportActionBar?.title = "Settings"

        val pinkTheme : ShapeableImageView = findViewById(R.id.coolPinkTheme)
        val blueTheme : ShapeableImageView = findViewById(R.id.coolBlueTheme)
        val greenTheme : ShapeableImageView = findViewById(R.id.coolGreenTheme)
        val purpleTheme : ShapeableImageView = findViewById(R.id.coolPurpleTheme)
        val blackTheme : ShapeableImageView = findViewById(R.id.coolBlackTheme)
        val versionName : TextView = findViewById(R.id.versionName)
        val sortBtn : ShapeableImageView = findViewById(R.id.sortBtn)

        when(MainActivity.themeIndex){
            0 -> {
                pinkTheme.setBackgroundColor(Color.YELLOW)
            }
            1 -> {
                blueTheme.setBackgroundColor(Color.YELLOW)
            }
            2 -> {
                greenTheme.setBackgroundColor(Color.YELLOW)
            }
            3 -> {
                purpleTheme.setBackgroundColor(Color.YELLOW)
            }
            4 -> {
                blackTheme.setBackgroundColor(Color.YELLOW)
            }
        }

        pinkTheme.setOnClickListener {
            saveTheme(0)
        }
        blueTheme.setOnClickListener {
            saveTheme(1)
        }
        greenTheme.setOnClickListener {
            saveTheme(2)
        }
        purpleTheme.setOnClickListener {
            saveTheme(3)
        }
        blackTheme.setOnClickListener {
            saveTheme(4)
        }
        versionName.text = setVersionDetails()
        sortBtn.setOnClickListener {
            val menuList = arrayOf("Recently Added", "Song Title", "File Size")
            var currentSort = MainActivity.sortOrder
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Sorting")
                .setPositiveButton("Ok"){_, _ ->
                    val editor = getSharedPreferences("SORTING", MODE_PRIVATE).edit()
                    editor.putInt("sortOrder", currentSort)
                    editor.apply()
                }
                .setSingleChoiceItems(menuList, currentSort){_, which->
                    currentSort = which
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        }
    }

    private fun saveTheme(index: Int){
        if (MainActivity.themeIndex != index) {
            val editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("themeIndex", index)
            editor.apply()
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Apply Theme")
                .setMessage("Do you want to apply theme?")
                .setPositiveButton("Yes"){_, _ ->
                    exitApp()
                }
                .setNegativeButton("No"){ dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
    }

    private fun setVersionDetails():String{
        return "Version Name : ${resources.getString(R.string.app_version_name)}"
    }
}