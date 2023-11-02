package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.musicplayer.databinding.ActivityPlayerBinding
import kotlin.system.exitProcess

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            ApplicationClass.PREVIOUS -> prevNextSong(false)
            ApplicationClass.PLAY -> if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextSong(true)
            ApplicationClass.EXIT -> exitApp()
        }
    }


    private fun playMusic() {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService?.mediaPlayer?.start()
        PlayerActivity.musicService?.showNotification(R.drawable.baseline_pause_24)
        PlayerActivity.binding.playPausebtnPA.setIconResource(R.drawable.baseline_pause_24)
    }

    private fun pauseMusic() {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.baseline_play_arrow_24)
        PlayerActivity.binding.playPausebtnPA.setIconResource(R.drawable.baseline_play_arrow_24)
    }

    private fun prevNextSong(increment : Boolean){
        setSongPosition(increment)
        PlayerActivity.musicService!!.createMediaPlayer()
        PlayerActivity.binding.songNamePA.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
        playMusic()
    }
}