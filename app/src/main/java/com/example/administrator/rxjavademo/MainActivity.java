package com.example.administrator.rxjavademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * create 操作符
         */
       //example01();


        /**
         * just 操作符
         */
       //example02();

        /**
         * from 操作符
         */
       //example03();

        /**
         * map 操作符
         *
         * 可以将被观察者发送的数据类型，变为其他的类型
         */

        //example04();

        /**
         * flatMap 操作符
         *
         * flatMap()与 Map()的方法类型,但是 flatMap() 返回 Observable
         *
         */

        initData();
        //example05();
    }

    /**
     * 构建数据
     */
    private void initData() {
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < 3 ; i++) {
            Student stu = new Student();
            stu.setId(i);
            stu.setName("学生" + i);
            List<Course> courseList = new ArrayList<>();
            for (int j = 0; j < 4 ; j++) {
                Course course = new Course();
                course.setId(j);
                course.setName(stu.getName() + "的课程" + j);
                courseList.add(course);
            }
            stu.setCourseList(courseList);
            list.add(stu);
        }

        Observable.
        fromIterable(list).
                /**
                 * 筛选学生，id 为 0 或者 2
                 */
                filter(new Predicate<Student>() {
                    @Override
                    public boolean test(Student student) throws Exception {
                        return student.getId() == 0 || student.getId() == 2;
                    }
                }).
                /**
                 * 将学生的课程发从出去，从学生实例得到课程实例，发送出去
                 */
                flatMap(new Function<Student, ObservableSource<Course>>() {
                    @Override
                    public ObservableSource<Course> apply(Student student) throws Exception {
                        Log.i(TAG, "===apply: " + student.getName());
                        return  Observable.
                                fromIterable(student.getCourseList()).
                                delay(10,TimeUnit.MILLISECONDS);
                    }
                }).
                /**
                 * 将得到的课程再筛选id = 1或 id= 3的
                 */
                filter(new Predicate<Course>() {
                    @Override
                    public boolean test(Course course) throws Exception {
                        return course.getId() == 1 || course.getId() == 3;
                    }
                }).
                subscribe(new Consumer<Course>() {
                    @Override
                    public void accept(Course course) throws Exception {
                        Log.i(TAG, "===accept: " + course.getName());
                    }
                });
    }

    private void example05() {
        List<Person> mPersonList = new ArrayList<>();
        List<Plan> plans = new ArrayList<>();
        Person person = new Person();
        person.setName("haha");
        person.setPlanList(plans);
        mPersonList.set(0,person);

        Observable.fromIterable(mPersonList)
                .flatMap(new Function <Person, ObservableSource<Plan>> () {
                    @Override
                    public ObservableSource <Plan> apply(Person person) {
                        return Observable.fromIterable(person.getPlanList());
                    }
                })
                .flatMap(new Function <Plan, ObservableSource <String>> () {
                    @Override
                    public ObservableSource <String> apply(Plan plan) throws Exception {
                        return Observable.fromIterable(plan.getActionList());
                    }
                }).
                subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "===onNext: " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "===onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "===onComplete: " + "加载完成");
                    }
                });

//        actionFlatMap();
    }

    private void example04() {
        Observable.
                just(1,2,3).
                map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return "I am a" + integer;
                    }
                }).
                subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.i(TAG, "===onNext: " + s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "===onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "===onComplete: " + "加载完成");
                    }
                });

    }

    private void example03() {
        Integer integer[] = {1,2,3,4,5,6};

        Observable.fromArray(integer).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "===onSubscribe: " + d.getClass().getName());
            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG, "===onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "===onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "===onComplete: " + "加载完成");
            }
        });

    }

    private void example01() {
        /**
         * 创建被观察者
         */
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                //创建ObservableOnSubscribe并且 重写subscribe方法后
                //通过发射器 emitter, 向观察者发送事件
                emitter.onNext("Hello Observer");
                emitter.onComplete();
            }
        });
        /**
         * 创建观察者
         *
         */
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "===onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "===onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "===onComplete: " + "加载完成");
            }
        };

        observable.subscribe(observer);
    }

    private void example02() {
        Observable.
        just(1,2,3).
        subscribe(new Observer<Integer>() {

            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "===onSubscribe: " + d);
            }

            @Override
            public void onNext(Integer integer) {
                Log.i(TAG, "===onNext: " + integer);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "===onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "===onComplete: " + "加载完成。。。");
            }
        });
    }


    public void actionFlatMap() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        Observable.
            fromIterable(list).
            flatMap(new Function<Integer, ObservableSource<String>>() {
                @Override
                public ObservableSource<String> apply(Integer integer) throws Exception {
                    Log.i(TAG, "===apply: " + "开始执行，第" + integer + "圆球的任务" + getThreadName());
                    return getObservable(integer);
                }
            }).
            subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(String s) {
                    Log.i(TAG, "===onNext: " + "已完成" + s + getThreadName());
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {
                    Log.i(TAG, "===onComplete: " + "加载完成");
                }
            });
    }

    private String getThreadName() {
        return "| ThreadName = " + Thread.currentThread().getName();
    }

    private ObservableSource<String> getObservable(final Integer integer) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("第" + integer + "圆球的第1个菱形任务");
                if (integer != -1){
                    //第2和第3个圆球的第二个延时任务
                    Thread.sleep(5000);
                }
                emitter.onNext("第" + integer + "圆球的第2个菱形任务");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread());
    }

}
