package pr3;

import io.reactivex.rxjava3.core.Observable;

import java.util.Arrays;
import java.util.Random;


class UserFriend {
    private int userId;
    private int friendId;

    @Override
    public String toString() {
        return "UserFriend{" +
                "userId=" + userId +
                ", friendId=" + friendId +
                '}';
    }

    public UserFriend(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    public int getUserId() {
        return userId;
    }

    public int getFriendId() {
        return friendId;
    }
}

class UserFriendService {
    private UserFriend[] friends;

    public UserFriendService(UserFriend[] friends) {
        this.friends = friends;
    }

    public Observable<UserFriend> getFriends(int userId) {
        return Observable.fromArray(friends).filter(friend -> friend.getUserId() == userId);
    }
}

class UserFriendGenerator {
    private static final Random RANDOM = new Random();
    private static final int NUM_USERS = 10; // число пользователей
    private static final int NUM_FRIENDS = 5; // максимальное число друзей для каждого пользователя

    public static UserFriend[] generateRandomUserFriends() {
        UserFriend[] friends = new UserFriend[NUM_USERS * NUM_FRIENDS];
        int index = 0;

        for (int userId = 1; userId <= NUM_USERS; userId++) {
            for (int i = 0; i < NUM_FRIENDS; i++) {
                int friendId = RANDOM.nextInt(NUM_USERS) + 1; // случайный friendId
                friends[index++] = new UserFriend(userId, friendId);
            }
        }
        return friends;
    }
}

public class Task3 {

    public static void main(String[] args) {
        // Генерация данных
        UserFriend[] friendsArray = UserFriendGenerator.generateRandomUserFriends();

        System.out.println(Arrays.toString(friendsArray));

        // Создание сервиса с массивом друзей
        UserFriendService userFriendService = new UserFriendService(friendsArray);

        // Пара случайных userId для примера
        int[] randomUserIds = {1, 2, 3, 4, 5};

        // Формирование потока из случайных userId
        Observable.fromArray(Arrays.stream(randomUserIds).boxed().toArray(Integer[]::new))
                .flatMap(userFriendService::getFriends)
                .subscribe(userFriend -> System.out.println("User ID: " + userFriend.getUserId() + ", Friend ID: " + userFriend.getFriendId()));
    }

}
