package edu.android.cardgame.level2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import edu.android.cardgame.R;

public class Result2Activity extends AppCompatActivity {

    private TextView textResult;
    private Button btnRetry;
    SharedPreferences spf = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        textResult = findViewById(R.id.textResult);
        btnRetry = findViewById(R.id.btnRetry);
        spf = getSharedPreferences("spfScore", MODE_PRIVATE);

        int score = getIntent().getIntExtra("score",-1);
        textResult.setText(String.valueOf(score));

        if(spf.getInt("spfscore",0) < score){ //내점수가 저번 점수보다 크면
            spf.edit().putInt("spfscore",score).commit(); //반영의 commit(). 현재상태 저장
            textResult.setText("신기록달성\n"+score);
        }


        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Result2Activity.this, Main2Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void Next(View view) {

        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
    }
}
