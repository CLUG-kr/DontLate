package com.example.dontlate_ver1;


import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Database {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    /* Insert */

    /* 주차 시 동영상이 떴을 경우 해당 주차, 수업 차수를 등록한다. finish 는 자동으로 0 으로 세팅된다. */
    public void insertWeek(String id, String course_name, String thisWeek, String due_date, int phase, int finish){
        Week week = new Week(due_date, phase, finish);
        databaseReference.child("user").child(id).child("take").child(course_name).child("week").child(thisWeek).setValue(week);
    }

    /* 디비에 저장된 유저별 수강 내역 , "마감기한/과목코드/주차/새끼동영상개수" 형식*/
    public interface dateCallback{
        void onCallback(ArrayList<String> value);
    }

    public void readData(dateCallback myCallback, String id){
        ArrayList<String> retList = new ArrayList<>();
        databaseReference.child("user").child(id).child("take").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot userSnapshot: snapshot.getChildren()){
                    String course_code = userSnapshot.getKey();

                    for(DataSnapshot userSnapshot2: userSnapshot.child("week").getChildren()) {
                        Week week = userSnapshot2.getValue(Week.class);
                        String ret = week.getDueDate()+"/"+course_code+"/"+userSnapshot2.getKey()+"/"+week.getPhase()+"/"+week.getFinish();
                        retList.add(ret);
                    }
                }
                myCallback.onCallback(retList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}



/* DB 구조에 필요한 class들 */
class User{
    public String id;
    public Take take;

    public User(){};

    public User(String id){
        this.id=id;
    }

    public User(Take take){
        this.take = take;
    }

}

class Take{
    public String course_name;
    public Week week;

    public Take(){
        this.course_name="Dummy";
    };

    public Take(String course_name){
        this.course_name = course_name;
    }

    public Take(String course_name, Week week){
        this.course_name = course_name;
        this.week = week;
    }
}

class Week{
    public String dueDate;
    public int phase;
    public int finish;

    public Week(){ };

    public Week(String due_date, int phase, int finish){
        this.dueDate = due_date;
        this.phase=phase;
        this.finish = finish;
    }

    public Week(int finish){
        this.finish = finish;
    }

    public String getDueDate(){
        return this.dueDate;
    }
    public int getPhase(){
        return this.phase;
    }
    public int getFinish(){
        return this.finish;
    }
}