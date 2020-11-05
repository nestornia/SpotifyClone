package com.example.spotifyclone.exoplayer

import com.example.spotifyclone.exoplayer.State.*

// This class will get all the songs from our firestore firebase and convert it to a format for our service

class FirebaseMusicSource {

    // mechanism to check when our songs have finished downloading
    //  list of lambda functions that take a boolean that tell us if the source was initialized or not
    // and then execute the source of code
    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = STATE_CREATED
        set(value) {
            if (value == STATE_INITIALIZED || value == STATE_ERROR) {
                // thread-safe way to get all the onReadyListeners
                synchronized(onReadyListeners) {
                    // field refers to the current value of the state
                    field = value
                    onReadyListeners.forEach{ listener ->
                        listener(state == STATE_INITIALIZED)
                    }
                } 
            }
        }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}