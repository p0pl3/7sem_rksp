package pr3;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Task1 {
    private static final int TEMPERATURE_THRESHOLD = 25;
    private static final int CO2_THRESHOLD = 70;

    public static void main(String[] args) {
        Random random = new Random();

        // Observable для датчика температуры
        Observable<Integer> temperatureSensor = Observable.interval(1, TimeUnit.SECONDS)
                .map(tick -> random.nextInt(16) + 15); // Случайное значение от 15 до 30

        // Observable для датчика CO2
        Observable<Integer> co2Sensor = Observable.interval(1, TimeUnit.SECONDS)
                .map(tick -> random.nextInt(71) + 30); // Случайное значение от 30 до 100

        // Объединяем оба потока данных
        Observable.combineLatest(
                        temperatureSensor,
                        co2Sensor,
                        (temperature, co2) -> new SensorData(temperature, co2)
                )
                .subscribe(new Observer<SensorData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // Подписка
                    }

                    @Override
                    public void onNext(SensorData data) {
                        System.out.println("Temperature: " + data.getTemperature());
                        System.out.println("CO2 level: " + data.getCo2());
                        if (data.getTemperature() > TEMPERATURE_THRESHOLD && data.getCo2() > CO2_THRESHOLD) {
                            System.out.println("ALARM!!!");
                        } else {
                            if (data.getTemperature() > TEMPERATURE_THRESHOLD) {
                                System.out.println("Warning: High temperature!");
                            }
                            if (data.getCo2() > CO2_THRESHOLD) {
                                System.out.println("Warning: High CO2 levels!");
                            }
                        }
                        System.out.println();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        // Завершение наблюдения
                    }
                });

        // Чтобы Main не завершался сразу
        try {
            Thread.sleep(30000); // Запускаем систему на 30 секунд
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Вспомогательный класс для хранения данных
    static class SensorData {
        private final int temperature;
        private final int co2;

        public SensorData(int temperature, int co2) {
            this.temperature = temperature;
            this.co2 = co2;
        }

        public int getTemperature() {
            return temperature;
        }

        public int getCo2() {
            return co2;
        }
    }
}
