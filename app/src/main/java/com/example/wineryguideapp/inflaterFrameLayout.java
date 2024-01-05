//package com.example.wineryguideapp;
//
//import static androidx.core.content.ContextCompat.startActivity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//
//public class inflaterFrameLayout extends View implements GestureDetector.OnGestureListener {
//
//
//        int minDistance=150;
//
//        int pageNum = MainActivity.pageNum;
//
//
//
//        private GestureDetector gestureDetector;
//
//        public inflaterFrameLayout(Context context) {
//            super();
//
//            gestureDetector = new GestureDetector(context, this);
//        }
//
//        @Override
//        public boolean onTouchEvent(MotionEvent event) {
//            return gestureDetector.onTouchEvent(event);
//        }
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return true;
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            float distanceX = e2.getX() - e1.getX();
//
//            if (Math.abs(distanceX) > minDistance) {
//                // Horizontal swipe detected
//                if (distanceX > 0) {
//
//                    if (pageNum < 3)
//                        pageNum++;
//                    else
//                        pageNum = 0;
//                }
//
//                else{
//                    if(pageNum>0)
//                        pageNum--;
//                    else
//                        pageNum=3;
//
//                    }
//            }
//            return false;
//        }
//
//        @Override
//        public void onLongPress(MotionEvent e) {
//            // Handle long press event here
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            // Handle scroll event here
//            return true;
//        }
//
//        @Override
//        public void onShowPress(MotionEvent e) {
//            // Handle show press event here
//        }
//
//        @Override
//        public boolean onSingleTapUp(MotionEvent e) {
//            // Handle single tap up event here
//            Intent intent = new Intent(MainActivity.this,MainActivity2.class);
//               startActivity(intent);
//            return true;
//        }
//    }
//
//}
