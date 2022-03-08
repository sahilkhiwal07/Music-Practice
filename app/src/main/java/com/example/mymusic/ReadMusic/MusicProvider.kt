package com.example.mymusic.ReadMusic

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.example.mymusic.Models.Music

private const val TAG = "My Music"

class MusicProvider(private val contentResolver: ContentResolver) {

    private lateinit var music: Music
    private var totalSongs = 0

    companion object {
        private val musicData = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA
        )

        private val albumArtUri = Uri.parse("content://media/external/audio/albumart")

        private var CONTENT_URL: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        private var selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun getAllAudioFromDevice(): List<Music> {
        val newMusicList: MutableList<Music> = ArrayList()

        val cr: ContentResolver = contentResolver

        cr.query(CONTENT_URL, musicData, selection, null, null)?.let { cursor ->

            totalSongs = cursor.count

            if (cursor.moveToNext()) {

                for (i in 0 until totalSongs) {

                    music = Music(
                        id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
                        title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
                        duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)),
                        album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        albumArtUri = ContentUris.withAppendedId(
                            albumArtUri,
                            cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                        ),
                        fileUri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)))

                    )

                    newMusicList.add(music)
                    cursor.moveToNext()
                }

                cursor.close()

            }

        }

        return newMusicList

    }

}