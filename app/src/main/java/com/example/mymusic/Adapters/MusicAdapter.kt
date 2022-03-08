package com.example.mymusic.Adapters

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymusic.Detailed
import com.example.mymusic.Models.Music
import com.example.mymusic.R

class MusicAdapter(
    private val context: Context
) : ListAdapter<Music, MusicAdapter.MusicHolder>(DIFF_ITEM_CALLBACK) {

    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<Music>() {
            override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.items, parent, false)
        return MusicHolder(view)
    }

    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        getItem(position)?.let { data ->
            holder.title.text = data.title
            holder.artist.text = data.artist

            val images: ByteArray? = getAlbum(data.fileUri)
            if (images != null) {
                Glide.with(context).load(images).into(holder.songImages)
            } else {
                Glide.with(context).load(R.drawable.ic_music).into(holder.songImages)
            }

            holder.cardView.setOnClickListener {
                context.startActivity(Detailed.newIntent(it.context,data))
            }
        }
    }

    class MusicHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: AppCompatTextView = itemView.findViewById(R.id.tv_title)
        val artist: AppCompatTextView = itemView.findViewById(R.id.tv_artist)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val songImages: ImageView = itemView.findViewById(R.id.songs_image)
    }

    // for taking default image form the source
    private fun getAlbum(uri: Uri): ByteArray? {
        val metaRetriever = MediaMetadataRetriever()

        metaRetriever.setDataSource(uri.toString())
        val art: ByteArray? = metaRetriever.embeddedPicture

        metaRetriever.release()
        return art
    }
}