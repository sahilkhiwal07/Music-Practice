package com.example.mymusic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymusic.Adapters.MusicAdapter
import com.example.mymusic.ReadMusic.MusicProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val My_Permission_Request_Music = 1
private const val EXTERNAL_STORAGE_PERMISSION_CODE = 23

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MusicAdapter
    private lateinit var musicProvider: MusicProvider

    companion object{
        val PERMISSION_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getRecyclerView()
        checkMusicPermission()

    }

    // Adapter
    private fun getRecyclerView() {
        adapter = MusicAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    // Reading data from device
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun readMusicFromDevice() {

        musicProvider = MusicProvider(contentResolver)

        lifecycleScope.launch(Dispatchers.Main) {
            val data = withContext(Dispatchers.IO) { musicProvider.getAllAudioFromDevice() }
            adapter.submitList(data)
        }
    }

    // Permissions
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkMusicPermission() {

        if (Build.VERSION.SDK_INT >= EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (ActivityCompat.checkSelfPermission( this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                } else {
                    ActivityCompat.requestPermissions(this, PERMISSION_STORAGE, My_Permission_Request_Music)
                }

            } else {
                readMusicFromDevice()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            My_Permission_Request_Music -> {
                if (permissions[0].contentEquals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,R.string.permission_granted,Toast.LENGTH_SHORT).show()
                    readMusicFromDevice()
                } else {
                    ActivityCompat.requestPermissions(this, PERMISSION_STORAGE, My_Permission_Request_Music)
//                    Toast.makeText(this,R.string.permission_not_granted,Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


}