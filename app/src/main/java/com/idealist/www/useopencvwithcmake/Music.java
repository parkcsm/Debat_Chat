//package com.idealist.www.useopencvwithcmake;
//
//import android.app.ProgressDialog;
//import android.media.MediaPlayer;
//import android.os.AsyncTask;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class Music extends AppCompatActivity {
//
//    /**
//     * 여기아래로 음악관련 부분
//     */
//    String streaming_time;
//    static seekbar_time_thread task;
//    boolean repeat_playing;
//    boolean repeat_playing_random;
//    boolean repeat_playing_all;
//    boolean round;
//    boolean plus, minus;
//    static boolean breaker;
//    LinearLayout LinearLayout_time;
//    boolean resume;
//    CheckBox button_convert_play_pause;
//    ProgressDialog simpleWaitDialog;
//    String streaming_time_now;
//    int song_number = 1;
//    String now_song;
//    static MediaPlayer mp; // 음악 재생을 위한 객체
//    int pos; // 재생 멈춘 시점
//    private Button activity_change;
//    private ImageView btn_previous_song;
//    private ImageView btn_next_song;
//    TextView name_of_song;
//    TextView tv_streaming_time_now;
//    TextView tv_streaming_time;
//    CheckBox btn_repeat;
//    CheckBox btn_repeat_all;
//    CheckBox btn_repeat_random;
//    SeekBar sb; // 음악 재생위치를 나타내는 시크바
//    static boolean isPlaying = false; // 재생중인지 확인할 변수
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_music);
//
//        /**여기부터 음악부분*/
//
//        btn_repeat = findViewById(R.id.repeat_playing);
//        btn_repeat_random = findViewById(R.id.repeat_playing_random);
//        btn_repeat_all = findViewById(R.id.repeat_playing_all);
//        btn_next_song = findViewById(R.id.next_song);
//        btn_previous_song = findViewById(R.id.previous_song);
//        name_of_song = findViewById(R.id.name_of_song);
//        tv_streaming_time = findViewById(R.id.tv_streaming_time);
//        tv_streaming_time_now = findViewById(R.id.tv_streaming_time_now);
//        button_convert_play_pause = findViewById(R.id.button_convert_play_pause);
//        LinearLayout_time = findViewById(R.id.LinearLayout_time);
//        sb = findViewById(R.id.seek_bar);
//
//        /**노래 선택하기전에 숨기는 것*/
//        btn_repeat_random.setVisibility(View.INVISIBLE);
//        btn_repeat.setVisibility(View.INVISIBLE);
//        btn_repeat_all.setVisibility(View.INVISIBLE);
//        sb.setVisibility(View.GONE);
//        LinearLayout_time.setVisibility(View.GONE);
//        btn_previous_song.setVisibility(View.INVISIBLE);
//        btn_next_song.setVisibility(View.INVISIBLE);
//
//        button_convert_play_pause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (((CheckBox) v).isChecked()) {
//                    if (pos > 0) {//이제 시작은 다 여기서
//                        /**재개*/
//                        mp.seekTo(pos); // 일시정지 시점으로 이동
//                        mp.start(); // 시작
//                    } else {
//                        /** 처음 재생*/
//                        /** 노래 선택하고 보여주는 것*/
//                        if (repeat_playing_random == true) {
//                            song_number = (int) ((Math.random() * 9) + 1);
//                        } else {
//                            song_number = 1;
//                        }
//                        btn_repeat_random.setVisibility(View.VISIBLE);
//                        btn_repeat.setVisibility(View.VISIBLE);
//                        btn_repeat_all.setVisibility(View.VISIBLE);
//                        sb.setVisibility(View.VISIBLE);
//                        LinearLayout_time.setVisibility(View.VISIBLE);
//                        btn_previous_song.setVisibility(View.VISIBLE);
//                        btn_next_song.setVisibility(View.VISIBLE);
//
//                        /**노래 제목에따른 재생시간과 시크바길이 결정*/
//                        PutSongName_SongStart_EndingEvent();
//                        name_of_song.setText(now_song);
//                        int a = mp.getDuration();  //노래 재생시간
//                        sb.setMax(a); // 씨크바의 최대범위를 노래의 재생시간으로 설정
//                        /**노래 시간 표시*/
//                        int minute = a / 1000 / 60;//분
//                        int second = a / 1000 % 60;///초
//                        if (second >= 10) {
//                            streaming_time = minute + ":" + second;
//                        } else {
//                            streaming_time = minute + ":0" + second;
//                        }
//                        tv_streaming_time.setText(streaming_time);
//
//                        /**잠시만기다려주세요 메세지*/
//                        /**노래재시작 + 쓰레드재시작(시크바이동 +시간표시)*/
//                        song_change_Task Task = new song_change_Task();
//                        Task.execute();
//                    }
//                } else {
//                    /**일시정지 버튼눌렀을때*/
//                    pos = mp.getCurrentPosition(); //현재 시간위치를 미리 받아온다.
//                    mp.pause(); //일시중지
//                }
//            }
//        });
//
//        btn_previous_song.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                /**실행중인 노래종료 + 쓰레드종료*/
//                task.cancel(true);
//                breaker = true;
//                isPlaying = false;
//                mp.stop();
//                mp.release();
//                /**재생안누르고 멈춘상태로 곡만 바뀔수도 있으니까 바뀌게해야함*/
//                button_convert_play_pause.setChecked(true);
//                /**노래 선택*/
//                /**노래제목에 따른 재생시간,시크바길이 설정*/
//                minus = true;
//                PutSongName_SongStart_EndingEvent();
//                name_of_song.setText(now_song);
//                int a = mp.getDuration(); // 노래의 재생시간 (miliSecond)
//                sb.setMax(a); // 씨크바의 최대범위를 노래의 재생시간으로 설정
//
//                /**노래 시간 설정*/
//                int minute = a / 1000 / 60;//분
//                int second = a / 1000 % 60;///초
//                if (second >= 10) {
//                    streaming_time = minute + ":" + second;
//                } else {
//                    streaming_time = minute + ":0" + second;
//                }
//                tv_streaming_time.setText(streaming_time);
//
//                /**잠시만기다려주세요 메세지*/
//                /**노래재시작 + 쓰레드재시작(시크바이동 +시간표시)*/
//                song_change_Task Task = new song_change_Task();
//                Task.execute();
//
//            }
//        });
//        btn_next_song.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                /**실행중인 노래종료 + 쓰레드종료*/
//                task.cancel(true);
//                breaker = true;
//                isPlaying = false;
//                mp.stop();
//                mp.release();
//                /**재생안누르고 멈춘상태로 곡만 바뀔수도 있으니까 바뀌게해야함*/
//                button_convert_play_pause.setChecked(true);
//                /**노래 선택*/
//
//                /**노래제목에 따른 재생시간,시크바길이 설정*/
//                plus = true;
//                PutSongName_SongStart_EndingEvent();
//                name_of_song.setText(now_song);
//                int a = mp.getDuration(); // 노래의 재생시간 (miliSecond)
//                sb.setMax(a); // 씨크바의 최대범위를 노래의 재생시간으로 설정
//
//                /**노래 시간 설정*/
//                int minute = a / 1000 / 60;//분
//                int second = a / 1000 % 60;///초
//                if (second >= 10) {
//                    streaming_time = minute + ":" + second;
//                } else {
//                    streaming_time = minute + ":0" + second;
//                }
//                tv_streaming_time.setText(streaming_time);
//
//                /**잠시만기다려주세요 메세지*/
//                /**노래재시작 + 쓰레드재시작(시크바이동 +시간표시)*/
//                song_change_Task Task = new song_change_Task();
//                Task.execute();
//            }
//
//        });
//
//        btn_repeat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btn_repeat_random.setChecked(false);
//                repeat_playing_random = false;
//                btn_repeat_all.setChecked(false);
//                repeat_playing_all = false;
//                if (repeat_playing == true) {
//                    mp.setLooping(false);
//                    repeat_playing = false;
//                    Toast.makeText(Music.this, "한곡 반복 비활성화", Toast.LENGTH_SHORT).show();
//                } else {
//                    mp.setLooping(true);
//                    repeat_playing = true;
//                    Toast.makeText(Music.this, "한곡 반복 활성화", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//
//        btn_repeat_all.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btn_repeat_random.setChecked(false);
//                repeat_playing_random = false;
//                btn_repeat.setChecked(false);
//                repeat_playing = false;
//                mp.setLooping(false);
//                if (repeat_playing_all == true) {
//                    repeat_playing_all = false;
//                    Toast.makeText(Music.this, "전체 반복 비활성화", Toast.LENGTH_SHORT).show();
//                } else {
//                    repeat_playing_all = true;
//                    Toast.makeText(Music.this, "전체 반복 활성화", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//
//        btn_repeat_random.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btn_repeat.setChecked(false);
//                repeat_playing = false;
//                mp.setLooping(false);
//                btn_repeat_all.setChecked(false);
//                repeat_playing_all = false;
//                if (repeat_playing_random == true) {
//                    repeat_playing_random = false;
//                    Toast.makeText(Music.this, "임의 반복 비활성화", Toast.LENGTH_SHORT).show();
//                } else {
//                    repeat_playing_random = true;
//                    Toast.makeText(Music.this, "임의 반복 활성화", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//
//        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (seekBar.getMax() == seekBar.getProgress()) { // 한곡반복누르면 해당되지 않음, 전체반복
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                mp.pause();
//            }
//
//            /**
//             *  SeekBar 컨트롤 -> 노래 위치변경, 노래 시작,중지-> 쓰레드가 노래위치에따라 현재시간, SeekBar위치 변경
//             * **/
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                int ttt = seekBar.getProgress();
//                if (ttt >= seekBar.getMax()) {
//                    mp.seekTo(seekBar.getMax());
//                } else {
//                    if (button_convert_play_pause.isChecked() == true) {
//                        mp.seekTo(ttt);
//                        mp.start();
//                    } else if (button_convert_play_pause.isChecked() == false) {
//                        mp.seekTo(ttt);
//                        pos = ttt;
//                        mp.pause();
//                    }
//                }
//            }
//        });
//    }
//
//    private void PutSongName_SongStart_EndingEvent() {
//
//        if (plus == true) {
//            if (repeat_playing_random == true) {
//                int randomValue = (int) (Math.random() * 9) + 1;
//                song_number = randomValue;
//                random_song_name();
//            } else {
//                song_number++;
//                if (song_number > 9) {
//                    if (repeat_playing_all == true) {
//                        song_number = 1;
//                    } else {
//                        song_number = 9;
//                    }
//                }
//                plus = false;
//            }
//        }
//
//        if (minus == true) {
//            if (repeat_playing_random == true) {
//                int randomValue = (int) (Math.random() * 9) + 1;
//                song_number = randomValue;
//                random_song_name();
//            } else {
//                song_number--;
//                if (song_number < 1) {
//                    if (repeat_playing_all == true) {
//                        song_number = 9;
//                    } else {
//                        song_number = 1;
//                    }
//                }
//                minus = false;
//            }
//        }
//
//
//        switch (song_number) {
//            case 1:
//                now_song = "1.Collapsed_tower";
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.collapsed_tower);
//                mp.start();
//                if (round == true) {
//                    button_convert_play_pause.setChecked(false);
//                    pos = 1;
//                    mp.pause();
//                    round = false;
//                }
//                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {
//
//                            song_number++;
//                            now_song = "2.City_of_twilight";
//
//
//                            if (song_number > 9) {
//                                button_convert_play_pause.setChecked(false);
//                                pos = 1;
//                                mp.pause();
//                                song_number = 1;
//                            }
//
////                            button_convert_play_pause.setChecked(false);
////                            pos = 1;
////                            mp.pause();
//                        }
//                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
//                            song_number++;
//                            now_song = "2.City_of_twilight";
//
//                            if (song_number > 9) {
//                                song_number = 1;
//                            }
//                        }
//
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
//                            int randomValue = (int) (Math.random() * 9) + 1;
//                            song_number = randomValue;
//                            random_song_name();
//                        }
//                        endding_event();
//                    }
//                });
//                break;
//            case 2:
//                now_song = "2.City_of_twilight";
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.city_of_twilight);
//
//                mp.start();
//                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {
//
//                            song_number++;
//                            now_song = "3.Velvetroom";
//
//                            if (song_number > 9) {
//                                button_convert_play_pause.setChecked(false);
//                                pos = 1;
//                                mp.pause();
//                                song_number = 1;
//                            }
//
////                            button_convert_play_pause.setChecked(false);
////                            pos = 1;
////                            mp.pause();
//                        }
//                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
//                            song_number++;
//                            now_song = "3.Velvetroom";
//
//                            if (song_number > 9) {
//                                song_number = 1;
//                            }
//                        }
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
//                            int randomValue = (int) (Math.random() * 9) + 1;
//                            song_number = randomValue;
//                            random_song_name();
//                        }
//                        endding_event();
//                    }
//                });
//                break;
//            case 3:
//                now_song = "3.Velvetroom";
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.velvetroom);
//
//                mp.start();
//                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {
//                            song_number++;
//                            now_song = "4.Crymore";
//
//
//                            if (song_number > 9) {
//                                button_convert_play_pause.setChecked(false);
//                                pos = 1;
//                                mp.pause();
//                                song_number = 1;
//                            }
//
////                            button_convert_play_pause.setChecked(false);
////                            pos = 1;
////                            mp.pause();
//                        }
//                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
//                            song_number++;
//                            now_song = "4.Crymore";
//
//                            if (song_number > 9) {
//                                song_number = 1;
//                            }
//                        }
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
//                            int randomValue = (int) (Math.random() * 9) + 1;
//                            song_number = randomValue;
//                            random_song_name();
//                        }
//                        endding_event();
//                    }
//                });
//                break;
//            case 4:
//                now_song = "4.Crymore";
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.crymore);
//                mp.start();
//
//                mp.start();
//                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {
//
//                            song_number++;
//                            now_song = "5.dream_music";
//
//                            if (song_number > 9) {
//                                button_convert_play_pause.setChecked(false);
//                                pos = 1;
//                                mp.pause();
//                                song_number = 1;
//                            }
//
////                            button_convert_play_pause.setChecked(false);
////                            pos = 1;
////                            mp.pause();
//                        }
//                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
//                            song_number++;
//                            now_song = "5.dream_music";
//
//                            if (song_number > 9) {
//                                song_number = 1;
//                            }
//                        }
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
//                            int randomValue = (int) (Math.random() * 9) + 1;
//                            song_number = randomValue;
//                            random_song_name();
//                        }
//                        endding_event();
//                    }
//                });
//                break;
//            case 5:
//                now_song = "5.dream_music";
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.dream_music);
//
//
//                mp.start();
//                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {
//
//                            song_number++;
//                            now_song = "6.Late_autumn";
//
//                            if (song_number > 9) {
//                                button_convert_play_pause.setChecked(false);
//                                pos = 1;
//                                mp.pause();
//                                song_number = 1;
//                            }
//
////                            button_convert_play_pause.setChecked(false);
////                            pos = 1;
////                            mp.pause();
//                        }
//                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
//                            song_number++;
//                            now_song = "6.Late_autumn";
//
//                            if (song_number > 9) {
//                                song_number = 1;
//                            }
//                        }
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
//                            int randomValue = (int) (Math.random() * 9) + 1;
//                            song_number = randomValue;
//                            random_song_name();
//                        }
//                        endding_event();
//                    }
//                });
//                break;
//            case 6:
//                now_song = "6.Late_autumn";
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.late_autumn);
//
//                mp.start();
//                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {
//
//                            song_number++;
//                            now_song = "7.Moment";
//
//                            if (song_number > 9) {
//                                button_convert_play_pause.setChecked(false);
//                                pos = 1;
//                                mp.pause();
//                                song_number = 1;
//                            }
//
////                            button_convert_play_pause.setChecked(false);
////                            pos = 1;
////                            mp.pause();
//                        }
//                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
//                            song_number++;
//                            now_song = "7.Moment";
//
//                            if (song_number > 9) {
//                                song_number = 1;
//                            }
//                        }
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
//                            int randomValue = (int) (Math.random() * 9) + 1;
//                            song_number = randomValue;
//                            random_song_name();
//                        }
//                        endding_event();
//                    }
//                });
//                break;
//            case 7:
//                now_song = "7.Moment";
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.moment);
//
//                mp.start();
//                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {
//
//                            song_number++;
//                            now_song = "8.Utakata";
//
//                            if (song_number > 9) {
//                                button_convert_play_pause.setChecked(false);
//                                pos = 1;
//                                mp.pause();
//                                song_number = 1;
//                            }
//
////                            button_convert_play_pause.setChecked(false);
////                            pos = 1;
////                            mp.pause();
//                        }
//                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
//                            song_number++;
//                            now_song = "8.Utakata";
//
//                            if (song_number > 9) {
//                                song_number = 1;
//                            }
//                        }
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
//                            int randomValue = (int) (Math.random() * 9) + 1;
//                            song_number = randomValue;
//                            random_song_name();
//                        }
//                        endding_event();
//                    }
//                });
//                break;
//            case 8:
//                now_song = "8.Utakata";
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.utakata);
//
//                mp.start();
//                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {
//
//                            song_number++;
//                            now_song = "9.Apparition";
//
//
//                            if (song_number > 9) {
//                                button_convert_play_pause.setChecked(false);
//                                pos = 1;
//                                mp.pause();
//                                song_number = 1;
//                            }
//
////                            button_convert_play_pause.setChecked(false);
////                            pos = 1;
////                            mp.pause();
//                        }
//                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
//                            song_number++;
//                            now_song = "9.Apparition";
//
//                            if (song_number > 9) {
//                                song_number = 1;
//                            }
//                        }
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
//                            int randomValue = (int) (Math.random() * 9) + 1;
//                            song_number = randomValue;
//                            random_song_name();
//                        }
//                        endding_event();
//                    }
//                });
//                break;
//            case 9:
//                now_song = "9.Apparition";
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.apparition);
//
//                mp.start();
//                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == false) {
//                            song_number++;
//                            now_song = "1.Collapsed_tower";
//
//                            if (song_number > 9) {
//                                button_convert_play_pause.setChecked(false);
//                                pos = 1;
//                                mp.pause();
//                                song_number = 1;
//                                round = true;
//                            }
////                            button_convert_play_pause.setChecked(false);
////                            pos = 1;
////                            mp.pause();
//                        }
//                        if (repeat_playing == false && repeat_playing_all == true && repeat_playing_random == false) {
//                            song_number++;
//                            now_song = "1.Collapsed_tower";
//
//                            if (song_number > 9) {
//                                song_number = 1;
//                            }
//                        }
//                        if (repeat_playing == false && repeat_playing_all == false && repeat_playing_random == true) {
//                            int randomValue = (int) (Math.random() * 9) + 1;
//                            song_number = randomValue;
//                            random_song_name();
//                        }
//                        endding_event();
//                    }
//                });
//                break;
//            default:
//                now_song = "1.Moment";
//                mp = MediaPlayer.create(getApplicationContext(), R.raw.moment);
//                break;
//        }
//        if (repeat_playing == true) {
//            mp.setLooping(true);
//        } else {
//            mp.setLooping(false);
//        }
//
//    }
//
//
//    class song_change_Task extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            simpleWaitDialog = ProgressDialog.show(Music.this, "잠시만 기다려주세요", "노래 변경중...");
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            simpleWaitDialog.dismiss();
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            /**시크바이동 + 시간표시 쓰레드 시작*/
//            breaker = false;
//            isPlaying = true; // 씨크바 쓰레드 반복하도록
//            task = new seekbar_time_thread();
//            task.execute();
//            /**노래 바꾸고 (다시) 시작*/
//        }
//    }
//
//    class seekbar_time_thread extends AsyncTask<Void, String, Void> {
//        @Override
//        protected Void doInBackground(Void... voids) {
//            while (isPlaying) {
//                if (breaker) {
//                    break;
//                }
//                if (mp != null) {
//                    sb.setProgress(mp.getCurrentPosition());
//                    if (breaker == true || mp == null) {
//                        break;
//                    }
//
//                    int time_now = mp.getCurrentPosition(); // 노래의 재생시간 (miliSecond)
//
//                    if (breaker == true || mp == null) {
//                        break;
//                    }
//                    if (mp.getCurrentPosition() <= mp.getDuration()) {
//                        //시간세팅
//                        int minute = time_now / 1000 / 60;//분
//                        int second = time_now / 1000 % 60;///초
//                        if (second >= 10) {
//                            streaming_time_now = minute + ":" + second;
//                        } else {
//                            streaming_time_now = minute + ":0" + second;
//                        }
//                        publishProgress(streaming_time_now);
//                    }
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(String... values) {
//            super.onProgressUpdate(values);
//            tv_streaming_time_now.setText(values[0]);
//        }
//    }
//
//    public void endding_event() {
//        //repeat_playing_all == true || repeat_playing_random == true
//        if (repeat_playing == false) {
//            /**실행중인 노래종료 + 쓰레드종료*/
//            task.cancel(true);
//            breaker = true;
//            isPlaying = false;
//            mp.stop();
//            mp.release();
//            /**재생안누르고 멈춘상태로 곡만 바뀔수도 있으니까 바뀌게해야함*/
//            button_convert_play_pause.setChecked(true);
//            /**노래 선택*/
//            /**노래제목에 따른 재생시간,시크바길이 설정*/
//            name_of_song.setText(now_song);
//
//            PutSongName_SongStart_EndingEvent();
//            int a = mp.getDuration(); // 노래의 재생시간 (miliSecond)
//            sb.setMax(a); // 씨크바의 최대범위를 노래의 재생시간으로 설정
//
//            /**노래 시간 설정*/
//            int minute = a / 1000 / 60;//분
//            int second = a / 1000 % 60;///초
//            if (second >= 10) {
//                streaming_time = minute + ":" + second;
//            } else {
//                streaming_time = minute + ":0" + second;
//            }
//            tv_streaming_time.setText(streaming_time);
//            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    song_change_Task Task = new song_change_Task();
//                    Task.execute();
//                }
//            });
//
//            //이상하게 다음곡 이전곡 넘어가기는 바로바로 되는데,
//            //여기서 Async를 쓰게되면 다음으로 잘 안넘어감
//
//        }
//
//    }
//
//    public void random_song_name() {
//        if (song_number == 1) {
//            now_song = "1.Collapsed_tower";
//        } else if (song_number == 2) {
//            now_song = "2.City_of_twilight";
//        } else if (song_number == 3) {
//            now_song = "3.Velvetroom";
//        } else if (song_number == 4) {
//            now_song = "4.Crymore";
//        } else if (song_number == 5) {
//            now_song = "5.dream_music";
//        } else if (song_number == 6) {
//            now_song = "6.Late_autumn";
//
//        } else if (song_number == 7) {
//            now_song = "7.Moment";
//        } else if (song_number == 8) {
//            now_song = "8.Utakata";
//        } else if (song_number == 9) {
//            now_song = "9.Apparition";
//
//        }
//
//    }
//
//}
