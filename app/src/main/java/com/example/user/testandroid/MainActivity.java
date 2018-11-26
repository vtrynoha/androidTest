package com.example.user.testandroid;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    NotificationManager notificationManager;
    int maxPage = 1;
    List<String> pages = new ArrayList<>();
    private GestureDetectorCompat gd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideMinus(maxPage);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        gd = new GestureDetectorCompat(this, this);
        gd.setOnDoubleTapListener(this);
    }

    public void minusPage(View view) {                                                              //Видалення вказаного елементу
        TextView pageNumber = findViewById(R.id.pageNumber);
        int currentPage = Integer.parseInt(pageNumber.getText().toString());
        notificationManager.cancel(currentPage);
        pages.remove(currentPage + "");
        if (currentPage == maxPage) maxPage = goToPreviousPage(currentPage);
        displayPage(goToPreviousPage(currentPage));
    }

    public void plusPage(View view) {                                                               //Створення нового елементу
        maxPage++;
        pages.add(maxPage + "");
        displayPage(maxPage);
    }

    public int goToNextPage(int currentPage) {                                                      //Перехід до наступного існуючого елементу
        for (int i = currentPage; i <= maxPage; i++) {
            if (pages.contains(currentPage + "")) {
                break;
            } else {
                currentPage++;
            }
        }
        return currentPage;
    }

    public int goToPreviousPage(int currentPage) {                                                  //Перехід до попереднього існуючого елементу
        for (int i = currentPage; i > 1; i--) {
            if (pages.contains(currentPage + "")) {
                break;
            } else {
                currentPage--;
            }
        }
        return currentPage;
    }

    public void createNotification(View view) {                                                     //Створення сповіщення
        TextView pageNumber = findViewById(R.id.pageNumber);
        String currentPage = pageNumber.getText().toString();
        int id = Integer.parseInt(currentPage);

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, id, resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "my_id")
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setColor(Color.BLUE)
                        .setContentTitle(getString(R.string.title_notification))
                        .setContentText(getString(R.string.text_notification) + currentPage)
                        .setContentIntent(resultPendingIntent);

        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_ALL;

        notificationManager.notify(id, notification);
    }

    public void displayPage(int page) {                                                             //Відображення елементу
        TextView pageNumber = findViewById(R.id.pageNumber);
        pageNumber.setText(String.valueOf(page).concat(""));
        hideMinus(page);
    }

    public void hideMinus(int page) {                                                               //Зміна видимості кнопки "мінус"
        ImageButton minusButton = findViewById(R.id.minusButton);
        if (page > 1) {
            minusButton.setVisibility(View.VISIBLE);
        } else {
            minusButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {      //Визначення перегортування елементів
        boolean result = false;

        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        result = onSwipeRight();
                    } else {
                        result = onSwipeLeft();
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    private boolean onSwipeLeft() {                                                                 //Перегортування вліво
        TextView pageNumber = findViewById(R.id.pageNumber);
        int currentPage = Integer.parseInt(pageNumber.getText().toString());
        if (currentPage < maxPage) {
            currentPage++;
            displayPage(goToNextPage(currentPage));
        } else {
            Toast.makeText(MainActivity.this, R.string.swipe_left_error, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private boolean onSwipeRight() {                                                                //Перегортування вправо
        TextView pageNumber = findViewById(R.id.pageNumber);
        int currentPage = Integer.parseInt(pageNumber.getText().toString());
        if (currentPage > 1) {
            currentPage--;
            displayPage(goToPreviousPage(currentPage));
        } else {
            Toast.makeText(MainActivity.this, R.string.swipe_right_error, Toast.LENGTH_SHORT).show();
        }
        return true;
    }


    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}
