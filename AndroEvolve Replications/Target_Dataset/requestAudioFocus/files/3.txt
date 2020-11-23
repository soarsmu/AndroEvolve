package de.vanmar.android.ilikepodcasts.library;

import static de.vanmar.android.ilikepodcasts.library.bo.ItemBuilder.anItem;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;

import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Environment;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;

import de.vanmar.android.ilikepodcasts.library.IMediaPlayerService.Callback;
import de.vanmar.android.ilikepodcasts.library.MediaPlayerService.MediaPlayerServiceBinder;
import de.vanmar.android.ilikepodcasts.library.bo.Item;
import de.vanmar.android.ilikepodcasts.library.playlist.PlaylistManager;

@RunWith(RobolectricTestRunner.class)
public class MediaPlayerServiceTest {
	private MediaPlayerService service;
	private MediaPlayerServiceBinder binder;

	private final Callback callback = mock(Callback.class);
	private final PlaylistManager playlistManager = mock(PlaylistManager.class);

	private MediaPlayer mediaPlayer;
	private final AudioManager audioManager = mock(AudioManager.class);

	@Before
	public void prepare() {
		mediaPlayer = spy(new MediaPlayer());
		service = new MediaPlayerService() {

			@Override
			protected MediaPlayer createMediaPlayer() {
				return mediaPlayer;
			}
		};
		service.playlistManager = playlistManager;
		service.audioManager = audioManager;
		binder = (MediaPlayerServiceBinder) service.onBind(null);
		binder.registerCallback(callback);
	}

	@Test
	public void shouldStartPlaying() throws Exception {
		// assume
		final String mediaPath = "test.mp3";

		// given
		final File mp3File = new File(
				Environment.getExternalStorageDirectory(), mediaPath);
		mp3File.createNewFile();
		mp3File.deleteOnExit();

		final Item item = anItem().withMediaPath(mediaPath).build();
		final int length = 123456;
		willReturn(length).given(mediaPlayer).getDuration();
		given(
				audioManager.requestAudioFocus(
						any(OnAudioFocusChangeListener.class),
						eq(AudioManager.STREAM_MUSIC),
						eq(AudioManager.AUDIOFOCUS_GAIN))).willReturn(
				AudioManager.AUDIOFOCUS_REQUEST_GRANTED);

		// when
		binder.play(item);

		// then
		verify(mediaPlayer).setDataSource(any(FileDescriptor.class));
		verify(mediaPlayer).prepare();
		verify(mediaPlayer).seekTo(item.getPosition());
		verify(mediaPlayer).start();
		verify(callback).playStarted(item, length);
	}

	@Test
	public void shouldRequestAudiofocus() throws SQLException, IOException {
		// given
		startPlay();

		// then
		verify(audioManager)
				.requestAudioFocus(any(OnAudioFocusChangeListener.class),
						eq(AudioManager.STREAM_MUSIC),
						eq(AudioManager.AUDIOFOCUS_GAIN));
	}

	@Test
	public void shouldPauseOnFocusLoss() throws IOException {
		// given
		startPlay();

		// then
		final ArgumentCaptor<OnAudioFocusChangeListener> captor = ArgumentCaptor
				.forClass(OnAudioFocusChangeListener.class);
		verify(audioManager)
				.requestAudioFocus(captor.capture(),
						eq(AudioManager.STREAM_MUSIC),
						eq(AudioManager.AUDIOFOCUS_GAIN));

		// when
		captor.getValue().onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS);

		// then
		verify(mediaPlayer).pause();
	}

	@Test
	public void shouldResumePlayOnRegainingFocus() throws IOException {
		// given
		startPlay();

		// then
		final ArgumentCaptor<OnAudioFocusChangeListener> captor = ArgumentCaptor
				.forClass(OnAudioFocusChangeListener.class);
		verify(audioManager)
				.requestAudioFocus(captor.capture(),
						eq(AudioManager.STREAM_MUSIC),
						eq(AudioManager.AUDIOFOCUS_GAIN));

		// when
		captor.getValue().onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS);

		// then
		verify(mediaPlayer).pause();

		// when
		captor.getValue().onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);

		// then
		verify(mediaPlayer).start();
	}

	@Test
	public void shouldNotResumePlayOnRegainingFocusWhenNotInterrupted()
			throws IOException {
		// given
		startPlay();
		binder.pause();

		// then
		final ArgumentCaptor<OnAudioFocusChangeListener> captor = ArgumentCaptor
				.forClass(OnAudioFocusChangeListener.class);
		verify(audioManager)
				.requestAudioFocus(captor.capture(),
						eq(AudioManager.STREAM_MUSIC),
						eq(AudioManager.AUDIOFOCUS_GAIN));

		// when
		captor.getValue().onAudioFocusChange(AudioManager.AUDIOFOCUS_LOSS);
		captor.getValue().onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);

		// then
		verify(mediaPlayer, never()).start();
	}

	@Test
	public void shouldPause() throws Exception {
		// given
		startPlay();

		// when
		binder.pause();

		// then
		verify(mediaPlayer).pause();
		verify(callback).playPaused();
	}

	@Test
	public void shouldPausePlayOnAudioBecomingNoisy() throws IOException {
		// given
		startPlay();

		// when
		Robolectric.shadowOf(service).sendBroadcast(
				new Intent(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

		// then
		verify(mediaPlayer).pause();
	}

	private void startPlay() throws IOException {
		final String mediaPath = "test.mp3";

		final File mp3File = new File(
				Environment.getExternalStorageDirectory(), mediaPath);
		mp3File.createNewFile();
		mp3File.deleteOnExit();

		final Item item = anItem().withMediaPath(mediaPath).build();
		final int length = 123456;
		willReturn(length).given(mediaPlayer).getDuration();
		given(
				audioManager.requestAudioFocus(
						any(OnAudioFocusChangeListener.class),
						eq(AudioManager.STREAM_MUSIC),
						eq(AudioManager.AUDIOFOCUS_GAIN))).willReturn(
				AudioManager.AUDIOFOCUS_REQUEST_GRANTED);

		binder.play(item);
		reset(mediaPlayer);
	}
}
