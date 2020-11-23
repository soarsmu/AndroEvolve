package com.a5androidintern2.p1281_audiofocus;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity implements OnCompletionListener {

    final static String LOG_TAG = "myLogs";

    final String DIR_SD = "MyFiles";
    final String FILENAME_SD = "music.mp3";

    AudioManager audioManager;

    AFListener afListenerMusic;
    AFListener afListenerSound;

    MediaPlayer mpMusic;
    MediaPlayer mpSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //получаем AudioManager. Именно через него мы будем запрашивать фокус.
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    public void onClickMusic(View view) {
        //метод срабатывает при нажатии кнопки Music.

        //создаём медиаплеер (для мелодии).
        mpMusic = new MediaPlayer();
        //try {
            //даём медиаплееру файл с музыкой.

            //mpMusic.setDataSource("mnt/sdcard/Music/music.mp3");
            //mpMusic.setDataSource("P1281_AudioFocus/app/src/main/res/raw/music.mp3");

/*



//мы ходим считать музыку из SD-карты.
//получаем доступ к SD-карте, отыскиваем там файл с музыкой.
//получам адрес файла на SD-карте.
//прописываем адрес файла в методе mpMusic.setDataSource().

            //проверяем доступность SD.
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Log.d(LOG_TAG, "SD-карта не доступна: " + Environment.getExternalStorageState());
                return;
            }
            //получаем путь к SD.
            File sdPath = Environment.getExternalStorageDirectory();
            //добавляем свой каталог к пути.
            sdPath = new File(sdPath.getAbsolutePath() + "/" + DIR_SD);
            //формируем объект File, который содержит путь к файлу.
            File sdFile = new File(sdPath, FILENAME_SD);


            Log.d(LOG_TAG, "адрес .mp3-файла: " + String.valueOf(sdFile));
            //даём медиаплееру файл с музыкой.
            mpMusic.setDataSource(String.valueOf(sdFile));

*/


            //mpMusic.setDataSource("sdcard/MyFiles/music.mp3");

            //mpMusic.setDataSource("P1281_AudioFocus/app/src/main/res/raw/music.mp3");


            //создаем MediaPlayer, который будет воспроизводить музыку из папки raw.
            mpMusic = MediaPlayer.create(this, R.raw.music);






            //mpMusic.prepare();
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        //устанавливаем Activity, как получателя уведомления об окончании воспроизведения.
        mpMusic.setOnCompletionListener(this);

        //Далее идет работа с фокусом.

        //afListenerMusic – это слушатель (реализующий интерфейс OnAudioFocusChangeListener),
        //который будет получать сообщения о потере/восстановлении фокуса.
        //Он является экземпляром класса AFListener, который мы рассмотрим чуть дальше.
        afListenerMusic = new AFListener(mpMusic, "Music");

        //Фокус запрашивается с помощью метода requestAudioFocus.
        //На вход необходимо передать:
        //- слушателя, который будет получать сообщения о фокусе
        //- тип потока
        //- тип фокуса
        int requestResult = audioManager.requestAudioFocus(
                afListenerMusic,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
        );
        //Метод requestAudioFocus возвращает статус:
        //AUDIOFOCUS_REQUEST_FAILED = 0 – фокус не получен
        //AUDIOFOCUS_REQUEST_GRANTED = 1 – фокус получен


        Log.d(LOG_TAG, "Music request focus, result: " + requestResult);

        //После того, как получили фокус, стартуем воспроизведение.
        mpMusic.start();
    }

    public void onClickSound(View view) {
        //метод срабатывает при нажатии на любую из 3-х кнопок.
        //Здесь мы определяем, какая из 3-х кнопок была нажата.


        //в переменную durationHint пишем тип аудио-фокуса, который будем запрашивать.
        int durationHint = AudioManager.AUDIOFOCUS_GAIN;
        switch (view.getId()) {
            case R.id.btnPlaySoundG:
                durationHint = AudioManager.AUDIOFOCUS_GAIN;
                break;
            case R.id.btnPlaySoundGT:
                durationHint = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;
                break;
            case R.id.btnPlaySoundGTD:
                durationHint = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK;
                break;
        }

        //создаем MediaPlayer, который будет воспроизводить наш звук взрыва из папки raw.
        mpSound = MediaPlayer.create(this, R.raw.explosion);
        //Присваиваем этому MediaPlayer-у слушателя окончания воспроизведения.
        mpSound.setOnCompletionListener(this);

        //Запрашиваем фокус с типом, который определили выше.
        afListenerSound = new AFListener(mpSound, "Sound");
        int requestResult = audioManager.requestAudioFocus(afListenerSound,
                AudioManager.STREAM_MUSIC, durationHint);
        Log.d(LOG_TAG, "Sound request focus, result: " + requestResult);

        //Стартуем воспроизведение.
        mpSound.start();

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //метод срабатывает по окончании воспроизведения.

        //определяем, какой именно MediaPlayer закончил играть
        //и методом abandonAudioFocus сообщаем системе,
        //что больше не претендуем на аудио-фокус.
        if (mp == mpMusic) {
            Log.d(LOG_TAG, "Music: abandon focus");
            audioManager.abandonAudioFocus(afListenerMusic);
        } else if (mp == mpSound) {
            Log.d(LOG_TAG, "Sound: abandon focus");
            audioManager.abandonAudioFocus(afListenerSound);
        }
    }

    @Override
    protected void onDestroy() {
        //освобождаем ресурсы и отпускаем фокус.
        super.onDestroy();
        if (mpMusic != null)
            mpMusic.release();
        if (mpSound != null)
            mpSound.release();
        if (afListenerMusic != null)
            audioManager.abandonAudioFocus(afListenerMusic);
        if (afListenerSound != null)
            audioManager.abandonAudioFocus(afListenerSound);
    }

    class AFListener implements OnAudioFocusChangeListener {
        //класс является получателем сообщений о потере/восстановлении фокуса.

        String label = "";
        MediaPlayer mp;

        public AFListener(MediaPlayer mp, String label) {
            this.label = label;
            this.mp = mp;
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            //Метод onAudioFocusChange получает на вход статус фокуса этого приложения.

            //При потерях фокуса AUDIOFOCUS_LOSS и AUDIOFOCUS_LOSS_TRANSIENT ставим паузу.
            //А при AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK – просто уменьшаем громкость.
            //При получении же фокуса (AUDIOFOCUS_GAIN)  возобновляем воспроизведение,
            //если оно было приостановлено, и ставим громкость на максимум.
            String event = "";
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    event = "AUDIOFOCUS_LOSS";
                    mp.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    event = "AUDIOFOCUS_LOSS_TRANSIENT";
                    mp.pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    event = "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK";
                    mp.setVolume(0.5f, 0.5f);
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    event = "AUDIOFOCUS_GAIN";
                    if (!mp.isPlaying())
                        mp.start();
                    mp.setVolume(1.0f, 1.0f);
                    break;
            }
            Log.d(LOG_TAG, label + " onAudioFocusChange: " + event);
        }
    }
}
