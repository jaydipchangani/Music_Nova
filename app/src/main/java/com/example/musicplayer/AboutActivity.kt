package com.example.musicplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        setContentView(R.layout.activity_about)

        supportActionBar?.title = "About"
        val aboutText :TextView = findViewById(R.id.aboutText)
        aboutText.text = aboutText()
    }
    private fun aboutText(): String{
        return "About\n" +
                "\n" +
                "Welcome to Music Nova, the ultimate destination for all your music needs! We offer a vast selection of songs from all genres and eras, so you're sure to find something you love. Plus, with our personalized recommendations and easy-to-use interface, you'll be able to discover new music and create custom playlists in no time.\n" +
                "\n" +
                "Whether you're looking to listen to your favorite hits or discover new artists, Music Nova has you covered. We also offer exclusive features like offline listening, high-quality audio, and ad-free listening, so you can enjoy your music without interruption.\n" +
                "\n" +
                "So what are you waiting for? Download Music Nova today and start listening to your favorite songs!"
    }
}