package com.amazonaws.lex.twilio.sample.conversation;

import com.amazonaws.lex.twilio.sample.streaming.EventsPublisher;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class BotConversation {

    private static final Logger LOG = Logger.getLogger(BotConversation.class);
    private final EventsPublisher eventsPublisher;
    private final ReentrantLock lock;

    private boolean conversationStopped;

    public BotConversation(EventsPublisher eventsPublisher) {
        this.eventsPublisher = eventsPublisher;
        this.conversationStopped = false;
        this.lock = new ReentrantLock();
    }

    public boolean isConversationStopped() {
        lock.lock();
        try {
            return conversationStopped;
        } finally {
            lock.unlock();
        }
    }

    public void writeUserInputAudio(ByteBuffer byteBuffer) {
        lock.lock();
        try {
            if (!conversationStopped) {
                eventsPublisher.writeUserInputAudio(byteBuffer);
            } else {
                LOG.debug("ignoring sending user input audio to server because conversation has stopped");
            }
        } finally {
            lock.unlock();
        }
    }

    public void informPlaybackFinished() {
        lock.lock();
        try {
            if (!conversationStopped) {
                eventsPublisher.playbackFinished();
            } else {
                LOG.warn("ignoring sending playback interruption to server because conversation has stopped");
            }
        } finally {
            lock.unlock();
        }
    }

    public void stopConversation() {
        lock.lock();
        try {
            if (!conversationStopped) {
                eventsPublisher.stop();
            }
            conversationStopped = true;
        } finally {
            lock.unlock();
        }
    }

}
