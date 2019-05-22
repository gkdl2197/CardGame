package edu.android.cardgame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final Context context = this;

    private static final String TAG = "tag";
    private static final int TOTAL_CARD_NUM = 12; // 카드 수
    public static final String SCORE_CHO = "score_cho";

    private int[] cardId = {R.id.card01, R.id.card02, R.id.card03, R.id.card04, R.id.card05, R.id.card06, R.id.card07,
            R.id.card08, R.id.card09, R.id.card10, R.id.card11, R.id.card12};

    private Card[] cardArray = new Card[TOTAL_CARD_NUM];

    private int CLICK_CNT = 0; // 클릭 카운트
    private Card first, second; // 첫번째 누른 버튼과 두번째 누른 버튼
    private int SUCCESS_CNT = 0; // 짝 맞추기 성공 카운트
    private boolean INPLAY = false; // 카드를 클릭할 수 있는지 여부

    //********
    private int CHO; // 초기 시간
    private static int scoreCho ; // 넘길 초

    //----------- 액티비티 위젯 -----------//
    private Button start;
    private TextView textTime;
    private TextView textCount;
    Thread thread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        for (int i = 0; i < TOTAL_CARD_NUM; i++) {
            cardArray[i] = new Card(i / 2); // 카드 생성
            findViewById(cardId[i]).setOnClickListener(this); // 카드 클릭 리스너 설정
            cardArray[i].card = findViewById(cardId[i]); // 카드 할당
            cardArray[i].onBack(); // 카드 뒤집어 놓음
        }

        textTime = findViewById(R.id.textTime);
        textCount = findViewById(R.id.textCount);

        CHO = 25;
        textTime.setText(CHO + "초");
        textCount.setText("0개");
        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                start.setVisibility(View.GONE);
                textCount.setVisibility(View.VISIBLE);

                thread = new Thread(new timeCheck());
                thread.start();
                startGame();
                //start.setBackgroundDrawable(background);
            }

        });

    } // end onCreate()

    protected void startDialog() {
        AlertDialog.Builder alt1 = new AlertDialog.Builder(this);
        alt1.setMessage("카드 짝 맞추기 게임입니다. 카드패를 잘 기억하시고 카드를 2개씩 뒤집어 짝을 맞추면 됩니다. 모든 짝을 맞추면 완료됩니다.")
                .setCancelable(false)
                .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alt2 = alt1.create();
        alt2.setTitle("게임 설명");
        alt2.show();
    }

    protected void clearDialog() {
        AlertDialog.Builder alt1 = new AlertDialog.Builder(this);
        alt1.setMessage("미션 성공! 다음 단계를 진행하세요~")
                .setCancelable(false)
                .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        
                        Log.i(TAG, "2");
                    }
                });
        AlertDialog alt2 = alt1.create();
        alt2.setTitle("짝 맞추기 완료");
        alt2.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startDialog();
    }

    @Override
    public void onClick(View v) {
        if (INPLAY) {
            switch (CLICK_CNT) {
                case 0: // 카드 하나만 뒤집었을 경우
                    for (int i = 0; i < TOTAL_CARD_NUM; i++) {
                        if (cardArray[i].card == v) {
                            first = cardArray[i];
                            break;
                        }
                    }
                    if (first.isBack) { // 이미 뒤집힌 카드는 처리 안함
                        first.onFront();
                        CLICK_CNT = 1;
                    }
                    break;
                case 1: // 카드 두개 뒤집었을 경우
                    for (int i = 0; i < TOTAL_CARD_NUM; i++) {
                        if (cardArray[i].card == (ImageButton) v) {
                            second = cardArray[i];
                            break;
                        }
                    }
                    if (second.isBack) { // 뒷면이 보이는 카드일 경우만 처리
                        second.onFront();

                        if (first.value == second.value) { // 짝이 맞은 경우
                            SUCCESS_CNT++;
                            textCount.setText(SUCCESS_CNT+"개(짝)");
                            Log.v("SUCCESS_CNT", "" + SUCCESS_CNT);
                            if (SUCCESS_CNT == TOTAL_CARD_NUM / 2) { // 모든 카드의 짝을 다 맞추었을 경우
                                scoreCho = CHO;
                                CHO = 0;
                                clearDialog();
                                //TODO

                            }
                        } else { // 짝이 틀릴 경우
                            Timer t = new Timer(0);
                            t.start();
                        }
                        CLICK_CNT = 0;
                    }
                    break;
            }
        }

    } // end onClick()

    public void startGame() {
        int[] random = new int[TOTAL_CARD_NUM];
        int x;

        for (int i = 0; i < TOTAL_CARD_NUM; i++) { // 모든 카드의 뒷면이 보이게 함
            if (!cardArray[i].isBack)
                cardArray[i].onBack();
        }

        boolean dup;
        for (int i = 0; i < TOTAL_CARD_NUM; i++) { // 0~15까지 랜덤한 순서로 random배열에 저장
            while (true) {
                dup = false;
                x = (int) (Math.random() * TOTAL_CARD_NUM);
                for (int j = 0; j < i; j++) {
                    if (random[j] == x) {
                        dup = true;
                        break;
                    }
                }
                if (!dup) break;
            }
            random[i] = x;
        }

        start.setClickable(false);
        for (int i = 0; i < TOTAL_CARD_NUM; i++) {
            cardArray[i].card = findViewById(cardId[random[i]]);
            cardArray[i].onFront();
        }
    // TODO 다 끝났을 때?
        Log.v("timer", "start");
        Timer t = new Timer(1);
        //flag = false;
        t.start();
		/*
		while(true) {
			if (flag) break;
			//Log.v("flag", "" + flag);
		}
		Log.v("timer", "end");
		*/

        SUCCESS_CNT = 0;
        CLICK_CNT = 0;
        INPLAY = true;
    } // end startGame()

    //TODO
    public void onClickReset(View view) {
        setResult(RESULT_OK);
        //TODO
        // ?? 1초 쉬었다가 시작되니까 일단 29로 설정해둠
        CHO = 24;
        start.setVisibility(View.VISIBLE);
        for (int i = 0; i < TOTAL_CARD_NUM; i++) {
            cardArray[i] = new Card(i / 2); // 카드 생성
            findViewById(cardId[i]).setOnClickListener(this); // 카드 클릭 리스너 설정
            cardArray[i].card = findViewById(cardId[i]); // 카드 할당
            cardArray[i].onBack(); // 카드 뒤집어 놓음
        }
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start.setVisibility(View.GONE);
                textCount.setVisibility(View.VISIBLE);

                thread = new Thread(new timeCheck());
                thread.start();
                startGame();
                //start.setBackgroundDrawable(background);
            }

        });

//                finish();
    } // end onClickReset()


    class Timer extends Thread {
        int kind;

        Timer(int kind) {
            super();
            this.kind = kind;
        }

        @Override
        public void run() {
            INPLAY = false;
            try {
                if (kind == 0) {
                    Thread.sleep(1000);
                    mHandler.sendEmptyMessage(0);
                } else if (kind == 1) {
                    Thread.sleep(3000);
                    mHandler.sendEmptyMessage(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            INPLAY = true;
        }

        Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
//                textTime.setText(CHO + "초");
                if (msg.what == 0) {
//                    startActivity(new Intent(MainActivity.this, ResultActivity.class));
                    thread.interrupt();
                    first.onBack();
                    second.onBack();
                    first.isBack = true;
                    second.isBack = true;
                    thread = new Thread(new timeCheck());
                    thread.start();
                } else if (msg.what == 1) {
                    //flag = true;
                    for (int i = 0; i < TOTAL_CARD_NUM; i++) {
                        cardArray[i].onBack();
                    }
                    start.setClickable(true);
                }

            }
        };

    }

    class Card {
        private final static int backImageID = R.drawable.game2_cardback;
        private final int[] frontImageID = {R.drawable.game2lv1_card01, R.drawable.game2lv1_card02, R.drawable.game2lv1_card03,
                R.drawable.game2lv1_card04, R.drawable.game2lv1_card05, R.drawable.game2lv1_card06};

        boolean isBack;
        int value;
        ImageButton card;

        Card(int value) {
            this.value = value;
        }

        public void onBack() {
            if (!isBack) {
                card.setBackgroundResource(backImageID);
                isBack = true;
            }
        }

        public void flip() { // 카드를 뒤집음
            if (!isBack) {
                card.setBackgroundResource(backImageID);
                isBack = true;
            } else {
                card.setBackgroundResource(frontImageID[value]);
                isBack = false;
            }
        }

        public void onFront() {
            if (isBack) {
                card.setBackgroundResource(frontImageID[value]);
                isBack = false;
            }
        }
    } // end class Card

    public class timeCheck implements Runnable {

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                textTime.setText(msg.arg1 + "초");

            if (msg.arg1 <= 0) {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.putExtra(SCORE_CHO, scoreCho);
                startActivity(intent);


                    Log.i(TAG, "1");

                    thread.interrupt();

                }

            }
        };

        @Override
        public void run() {
            int i = 25;
            while (true) {
                try {
                    Message msg = new Message();
                    msg.arg1 = CHO--;

                    Thread.sleep(1000);
                     handler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

    } // end class timeCheck

} // end class MainActivity
