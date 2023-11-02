package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlin.system.exitProcess

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        lateinit var musicListPA : ArrayList<Music>
        var songPosition : Int = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null
        var repeat: Boolean = false
        var min15 : Boolean = false
        var min30 : Boolean = false
        var min60 : Boolean = false
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //for service
        val intentService = Intent(this, MusicService::class.java)
        bindService(intentService, this, BIND_AUTO_CREATE)
        startService(intentService)

        val backBtn : ImageButton = findViewById(R.id.backBtnPA)
        backBtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        val playPause : ExtendedFloatingActionButton = findViewById(R.id.playPausebtnPA)
        playPause.setOnClickListener {
            if (isPlaying) pauseMusic()
            else playMusic()
        }
        val previous : ExtendedFloatingActionButton = findViewById(R.id.previousbtnPA)
        val next : ExtendedFloatingActionButton = findViewById(R.id.nextbtnPA)
        previous.setOnClickListener {
            prevNextSong(false)
        }
        next.setOnClickListener {
            prevNextSong(true)
        }
        musicListPA = ArrayList()
        initializeLayout()
        backBtn.setOnClickListener {
            finish()
        }
        val seekBarPA : AppCompatSeekBar = findViewById(R.id.seekBarPA)
        seekBarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    musicService!!.mediaPlayer!!.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        val repeatBtnPA : ImageButton = findViewById(R.id.repeatBtnPA)
        repeatBtnPA.setOnClickListener {
            if (!repeat){
                repeat = true
                repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            }
            else{
                repeat = false
                repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
            }
        }
        val equalizerBtnPA : ImageButton = findViewById(R.id.equalizerBtnPA)
        equalizerBtnPA.setOnClickListener {
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 13)
            }
            catch (e: Exception){
                Toast.makeText(this, "Equalizer feature not supported!!", Toast.LENGTH_SHORT).show()
            }
        }
        val timerBtnPA : ImageButton = findViewById(R.id.timerBtnPA)
        timerBtnPA.setOnClickListener {
            val timer = min15 || min30 || min60
            if (!timer) {
                showBottomSheetDialog()
            }
            else{
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer")
                    .setMessage("Do you want to Stop Timer?")
                    .setPositiveButton("Yes"){_, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
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
        val shareBtnPA : ImageButton = findViewById(R.id.shareBtnPA)
        shareBtnPA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent,"Sharing Music File!!"))
        }
    }

    private fun setLayout(){
        if (musicListPA.isNotEmpty() && songPosition < musicListPA.size) {
            binding.songNamePA.text = musicListPA[songPosition].title
        }
        val repeatBtnPA : ImageButton = findViewById(R.id.repeatBtnPA)
        val timerBtnPA : ImageButton = findViewById(R.id.timerBtnPA)
        if(repeat){
            repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        }
        if (min15 || min30 || min60){
            timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        }
    }

    private fun createMediaPlayer(){
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            val playPause : ExtendedFloatingActionButton = findViewById(R.id.playPausebtnPA)
            playPause.setIconResource(R.drawable.baseline_pause_24)
            musicService!!.showNotification(R.drawable.baseline_pause_24)

            val seekBarPA : AppCompatSeekBar = findViewById(R.id.seekBarPA)
            val tvSeekBarStart : TextView = findViewById(R.id.tvSeekBarStart)
            val tvSeekBarEnd : TextView = findViewById(R.id.tvSeekBarEnd)
            tvSeekBarStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            tvSeekBarEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            seekBarPA.progress = 0
            seekBarPA.max = musicService!!.mediaPlayer!!.duration
            musicService!!.mediaPlayer!!.setOnCompletionListener(this)
        }
        catch (e:Exception){
            return
        }
    }
    private fun initializeLayout(){
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")){
            "MusicAdapterSearch" -> {
                musicListPA.addAll(MainActivity.musicListSearch)
                setLayout()
            }
            "MusicAdapter" -> {
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
            }
            "MainActivity" -> {
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
            }
        }
    }

    private fun playMusic(){
        val playPause : ExtendedFloatingActionButton = findViewById(R.id.playPausebtnPA)
        isPlaying = true
        musicService!!.mediaPlayer!!.start()
        playPause.setIconResource(R.drawable.baseline_pause_24)
        musicService!!.showNotification(R.drawable.baseline_pause_24)
    }
    private fun pauseMusic(){
        val playPause : ExtendedFloatingActionButton = findViewById(R.id.playPausebtnPA)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
        playPause.setIconResource(R.drawable.baseline_play_arrow_24)
        musicService!!.showNotification(R.drawable.baseline_play_arrow_24)
    }
    private fun prevNextSong(increment: Boolean){
        if(increment){
            setSongPosition(true)
            setLayout()
            createMediaPlayer()
        }
        else{
            setSongPosition(false)
            setLayout()
            createMediaPlayer()
        }
    }


    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        if (musicService == null) {
            val binder = service as MusicService.MyBinder
            musicService = binder.currentService()
        }
        createMediaPlayer()
        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        setSongPosition(true)
        createMediaPlayer()
        try {
            setLayout()
        }
        catch (e : Exception){
            return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 13 || resultCode == RESULT_OK){
            return
        }
    }

    private fun showBottomSheetDialog(){
        val timerBtnPA : ImageButton = findViewById(R.id.timerBtnPA)
        val dialog = BottomSheetDialog(this@PlayerActivity)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 15 minutes!!", Toast.LENGTH_SHORT).show()
            timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min15 = true
            Thread{Thread.sleep(15 * 60000)
            if (min15){
                exitApp()
            }}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 30 minutes!!", Toast.LENGTH_SHORT).show()
            timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min30 = true
            Thread{Thread.sleep(30 * 60000)
                if (min30){
                    exitApp()
                }}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 60 minutes!!", Toast.LENGTH_SHORT).show()
            timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min60 = true
            Thread{Thread.sleep(60 * 60000)
                if (min60){
                    exitApp()
                }}.start()
            dialog.dismiss()
        }
    }
}