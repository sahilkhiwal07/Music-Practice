package com.example.mymusic

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mymusic.Models.Music
import kotlinx.android.synthetic.main.activity_detailed.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "tag"

class Detailed : AppCompatActivity() {

    private lateinit var music: Music
    private var handler = Handler()

    companion object {

        private var player: MediaPlayer? = null

        private const val EXTRA_MUSIC = "EXTRA_MUSIC"

        fun newIntent(context: Context, music: Music): Intent {
            val intent = Intent(context, Detailed::class.java)
            intent.putExtra(EXTRA_MUSIC, music)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        music = intent.getParcelableExtra<Music>(EXTRA_MUSIC)!!

        myScreenUI()
        playingFirstTimeMusic()

    }

    private fun myScreenUI() {

        tv_song.text = music.title

        var images: ByteArray? = getMyAlbum(music.fileUri)
        if (images != null) {
            Glide.with(this).load(images).into(image_Second)
        } else {
            Glide.with(this).load(R.drawable.headphones).into(image_Second)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (player != null && fromUser) player?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

    }

    private fun playingFirstTimeMusic() {

        val uri = Uri.parse(music.fileUri.toString())

        if (player != null) {
            stop()
        } else {
            player = MediaPlayer.create(this, uri)
            initializeSeekbar()
            player?.setOnCompletionListener {
                onSongComplete()
            }
            play()
            btn_play.setBackgroundResource(R.drawable.ic_pause)
        }

    }

    private fun initializeSeekbar() {

        seekBar.max = player!!.duration

        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    seekBar.progress = player!!.currentPosition
                    updatePlayer(player!!.currentPosition)
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    seekBar.progress = 0
                }
            }

        }, 0)
    }

    private fun updatePlayer(currentDuration: Int) {
        tv_start.text = "" + milliSecondsToTimer(currentDuration.toLong())
    }

    private fun milliSecondsToTimer(milliseconds: Long): String? {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"

        // return timer string
        return finalTimerString
    }

    override fun onResume() {
        playThreadBtn()
        super.onResume()
    }

    private fun playThreadBtn() {
        lifecycleScope.launch(Dispatchers.IO) {
            btn_play.setOnClickListener {
                playPausedButtonClicked()
            }
        }
    }

    private fun playPausedButtonClicked() {
        if (player?.isPlaying!!) {
            btn_play.setBackgroundResource(R.drawable.ic_play)
            pause()
            initializeSeekbar()
        } else {
            btn_play.setBackgroundResource(R.drawable.ic_pause)
            play()
            initializeSeekbar()
        }
    }


    // Getting Song images for Ui screen
    private fun getMyAlbum(uri: Uri): ByteArray? {

        val metaRetriever = MediaMetadataRetriever()
        metaRetriever.setDataSource(uri.toString())

        var art: ByteArray? = metaRetriever.embeddedPicture

        metaRetriever.release()
        return art
    }

    // button actions
    private fun play() {
        player?.start()
    }

    private fun pause() {
        player?.pause()
    }

    private fun stop() {
        player?.stop()
        player?.release()
        player = null
    }

    private fun onSongComplete() {
        // stop the music
        if (player != null) {
            player?.release()
            player = null
            Toast.makeText(this, "Song Completed", Toast.LENGTH_SHORT).show()
            btn_play.setBackgroundResource(R.drawable.ic_play)
        }
    }


}




