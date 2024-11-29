package edu.arthur.montyhallproblem;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

	static final int N_DOORS = 3;

	private final int id;
	private final int winDoor;

	private int playerFirstChoice;
	private int openDoor;
	private int playerFinalChoice;

	public Game(int gameId) {
		id = gameId;
		winDoor = getRandomDoor();
		playerFirstChoice = -1;
		openDoor = -1;
		playerFinalChoice = -1;
	}

	public void playerMakeFirstChoice() {
		if (isValidByRange(playerFirstChoice)) {
			throw new RuntimeException("Игрок уже делал первый выбор.");
		}
		playerFirstChoice = getRandomDoor();
	}

	public void masterOpensABadDoor() {
		if (!isValidByRange(playerFirstChoice)) {
			throw new RuntimeException("Игрок ещё не сделал первый выбор.");
		}
		openDoor = getRandomDoor(new int[] { winDoor, playerFirstChoice });
	}

	public void playerMakeSecondChoice(boolean swapDoor) {
		if (!isValidByRange(playerFirstChoice)) {
			throw new RuntimeException("Игрок ещё не сделал первый выбор.");
		}
		if (!isValidByRange(openDoor)) {
			throw new RuntimeException("Ведущий ещё не открыл проигрышную дверь.");
		}

		if (swapDoor) {
			playerFinalChoice = getRandomDoor(new int[] { playerFirstChoice, openDoor });
		} else {
			playerFinalChoice = playerFirstChoice;
		}
	}

	public boolean isPlayerWinner() {
		if (!isValidByRange(playerFinalChoice)) {
			throw new RuntimeException("Ещё не пройдены все этапы игры.");
		}

		return playerFinalChoice == winDoor;
	}

	public GameDescriptor getGameDescriptor() {
		if (!isValidByRange(playerFinalChoice)) {
			throw new RuntimeException("Ещё не пройдены все этапы игры.");
		}

		return new GameDescriptor(id, winDoor, playerFirstChoice, openDoor, playerFinalChoice);
	}

	private static int getRandomDoor(int[] exclusions) {
		// Нормализуем исключения -- должны быть только уникальные индексы дверей
		// в порядке возрастания (для удобства последующего маппинга индекса):
		exclusions = Arrays.stream(exclusions).sorted().distinct().toArray();
		int doorsRemaining = N_DOORS - exclusions.length;
		int indexAmongRemaining = ThreadLocalRandom.current().nextInt(doorsRemaining);
		// Мапим случайный индекс в корректный индекс двери из диапазона оставшихся
		// с учётом исключений:
		for (int exclDoorIndex : exclusions) {
			if (indexAmongRemaining >= exclDoorIndex) {
				++indexAmongRemaining;
			}
		}
		return indexAmongRemaining;
	}

	private static int getRandomDoor() {
		return ThreadLocalRandom.current().nextInt(N_DOORS);
	}

	public static boolean isValidByRange(int door) {
		return door >= 0 && door < N_DOORS;
	}

}
