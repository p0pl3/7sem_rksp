package pr3;


import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class File {
    private String type;
    private int size;

    public File(String type, int size) {
        this.type = type;
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }
}

class FileGenerator {
    private static final String[] FILE_TYPES = {"XML", "JSON", "XLS"};

    public static Observable<File> generateFiles() {
        return Observable.create(emitter -> {
            while (!emitter.isDisposed()) {
                // Генерация случайного размера файла
                int size = ThreadLocalRandom.current().nextInt(10, 101);
                // Генерация случайного типа файла
                String type = FILE_TYPES[ThreadLocalRandom.current().nextInt(FILE_TYPES.length)];
                // Создание файла
                File file = new File(type, size);
                // Эмит файла
                emitter.onNext(file);
                // Задержка от 100 до 1000 мс
                int delay = ThreadLocalRandom.current().nextInt(100, 1001);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    emitter.onError(e);
                }
            }
        });
    }
}

class FileQueue {
    private final PublishSubject<File> queue = PublishSubject.create();
    private final List<File> files = new ArrayList<>();
    private static final int CAPACITY = 5;

    public void addFile(File file) {
        if (files.size() < CAPACITY) {
            files.add(file);
            queue.onNext(file);
        } else {
            System.out.println("Очередь заполнена! Файл: " + file.getType() + " отбрасывается.");
        }
    }

    public PublishSubject<File> getQueue() {
        return queue;
    }

    public void removeFile(File file) {
        files.remove(file);
    }
}

class FileHandler {
    private final String type;

    public FileHandler(String type) {
        this.type = type;
    }

    public Observable<String> processFile(File file) {
        return Observable.create(emitter -> {
            if (file.getType().equals(type)) {
                int processingTime = file.getSize() * 10;
                try {
                    Thread.sleep(processingTime);
                    emitter.onNext("Обработан файл типа: " + file.getType() + " с размером: " + file.getSize());
                } catch (InterruptedException e) {
                    emitter.onError(e);
                }
                emitter.onComplete();
            } else {
                emitter.onComplete(); // Неподходящий файл не обрабатывается
            }
        });
    }
}

public class Task4 {

    public static void main(String[] args) {

        FileQueue fileQueue = new FileQueue();

// Обработка файлов из очереди
        fileQueue.getQueue().subscribe(file -> {
            FileHandler handler = new FileHandler(file.getType());
            handler.processFile(file).subscribe(
                    System.out::println,
                    Throwable::printStackTrace,
                    () -> fileQueue.removeFile(file) // Удаление файла из очереди после обработки
            );
        });
        // Генератор файлов
        Disposable generatorDisposable = FileGenerator.generateFiles()
                .subscribe(fileQueue::addFile);

        // Программа будет работать 10 секунд
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        generatorDisposable.dispose();
        System.exit(0);
    }
}
