package com.example.spotifyclone.exoplayer

import android.app.PendingIntent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.inject.Inject

private const val SERVICE_TAG = "MusicService"

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    // injecting dependency from service module for the data source factory
    // if you go to servicemodule it has the same object
    // this will fetch our data from firebase
    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    // injecting dependency from service module for the exoPlayer instance
    @Inject
    lateinit var exoPlayer: SimpleExoPlayer

    // declaring a specific scope exclusively for the service
    // it is limited to the lifetime of the service
    private val serviceJob =  Job()
    // declaring the scope for the service to be launched with coroutines
    // deals with the cancelation of the coroutines for not having memory leaks
    // what this means its going to merge the coroutine scope that has the main dispatcher and the service job together
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    // if we want to play music with that mediaBrowserCompat
    // we have a session for the media and we use it to communicate with the servicee
    private lateinit var mediaSession: MediaSessionCompat
    // class used to connect to that media session
    private lateinit var mediaSessionConnector: MediaSessionConnector

    override fun onCreate() {
        super.onCreate()
        // first get the activity intent for the notification leading to the activity
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            // constructing a pending intent - it is the intent we just created in the context of this class
            PendingIntent.getActivity(this, 0, it, 0)
        }
        // defining the media session
        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            // setting the session with the pending intent we just created
            setSessionActivity(activityIntent)
            // as active
            isActive = true
        }

        // assign the token of our just created media session to our service
        sessionToken = mediaSession.sessionToken

        // we need some way to connect to our media session
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        // we are using the connector to the player we are injecting from our serviceModule
        mediaSessionConnector.setPlayer(exoPlayer)

    }

    override fun onDestroy() {
        super.onDestroy()
        // just making sure to cancel all the corouintes that launch on the service scope once it dies
        serviceScope.cancel()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        TODO("Not yet implemented")
    }

    override fun onLoadChildren(
        // eg. playlists, albums, recommended sections, whatever
        // parent id we can call to get a list of songs
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }

}