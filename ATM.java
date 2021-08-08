package com.oportun;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.InputMismatchException;

public class ATM {

	private enum OPERATION {
		WITHDRAW, DEPOSIT
	};

	private static final Integer[] denominations = { 20, 10, 5, 1 };

	private BigDecimal totalBalance = BigDecimal.ZERO;

	private static Map<Integer, Integer> balanceMap = new TreeMap<>();

	Scanner scanner = new Scanner(System.in);

	static void loadAccount() {
		for (Integer denomination : denominations) {
			balanceMap.put(denomination, 0);
		}
	}

	public void withdraw() {
		Map<Integer, Integer> withdrawMap = new HashMap<>();
		System.out.print("Withdraw : ");
		Integer amount = scanner.nextInt();
		amount = amount != null ? amount : 0;
		System.out.println("------------------------------------------");

		withdrawMap = distributeAmount(amount);
		if (validateWithdrawl(withdrawMap))
			updateBalance(withdrawMap, OPERATION.WITHDRAW);
		nextStep();
	}

	public void deposit() {
		Map<Integer, Integer> depositMap = new HashMap<>();
		Integer dTotalAmount = 0;
		for (Integer denomination : denominations) {
			System.out.print("Desposit " + denomination + "s : ");
			Integer note = scanner.nextInt();
			note = note != null ? note : 0;
			depositMap.put(denomination, note);
			dTotalAmount += denomination * note;
		}
		System.out.println("------------------------------------------");
		if (validateDeposit(depositMap)) {
			updateBalance(depositMap, OPERATION.DEPOSIT);
		}
		nextStep();
	}

	private void updateBalance(Map<Integer, Integer> newMap, OPERATION operation) {
		if (operation.equals(OPERATION.DEPOSIT)) {
			newMap.forEach((k, v) -> {
				Integer oldNotes = balanceMap.get(k);
				Integer newNotes = oldNotes != null ? oldNotes + v : v;
				balanceMap.put(k, newNotes);
			});
			totalBalance = totalBalance.add(new BigDecimal(totalAmount(newMap)));
			System.out.println("Balance: " + balanceMap + ", Total = " + totalBalance);
			System.out.println("--------------------------------------------------------------------");
		}

		if (operation.equals(OPERATION.WITHDRAW)) {
			newMap.forEach((k, v) -> {
				Integer oldNotes = balanceMap.get(k);
				Integer newNotes = oldNotes - v;
				balanceMap.put(k, newNotes);
			});
			System.out.println("Dispensed: " + newMap);

			totalBalance = totalBalance.subtract(new BigDecimal(totalAmount(newMap)));
			System.out.println("Balance: " + balanceMap + "Total : " + totalBalance);
			System.out.println("---------------------------------------------------------------------");
		}

	}

	private boolean validateDeposit(Map<Integer, Integer> depositMap) {
		boolean result = true;
		for (Map.Entry<Integer, Integer> entry : depositMap.entrySet()) {
			Integer depositCount = entry.getValue() != null ? entry.getValue() : 0;

			if (depositCount <= 0) {
				throw new MyException("Incorect Deposit amount");

			}
		}
		if (totalAmount(depositMap) <= 0) {
			throw new MyException("Deposit amount cannot be zero");
		}
		return result;
	}

	private Map<Integer, Integer> distributeAmount(Integer amount) {
		Map<Integer, Integer> withdrawlMap = new HashMap<>();
		for (int i = 0; i < denominations.length && amount != 0; i++) {
			Integer deductedDenominationCount = 0;
			if (amount >= denominations[i] && balanceMap.get(denominations[i]) > 0) {
				deductedDenominationCount = amount / denominations[i] <= balanceMap.get(denominations[i])
						? amount / denominations[i]
						: balanceMap.get(denominations[i]);
				withdrawlMap.put(denominations[i], deductedDenominationCount);
			}
			amount = amount - denominations[i] * deductedDenominationCount;
		}
		// System.out.println(withdrawlMap);
		// System.out.println("Total splitted"+totalAmount(withdrawlMap));
		if (amount > 0)
			throw new MyException("Requested Withdraw Amount is not dispensable");
		return withdrawlMap;
	}

	private boolean validateWithdrawl(Map<Integer, Integer> newMap) {
		boolean result = true;
		for (Map.Entry<Integer, Integer> entry : newMap.entrySet()) {
			Integer withdrawCount = entry.getValue() != null ? entry.getValue() : 0;
			Integer balanceCount = balanceMap.get(entry.getKey());

			if (withdrawCount <= 0 || balanceCount < withdrawCount)
				throw new MyException("Incorect or insufficient funds");
		}
		if (totalAmount(newMap) > totalAmount(balanceMap))
			throw new MyException("Incorect or insufficient funds");

		return result;
	}

	private Integer totalAmount(Map<Integer, Integer> denominationMap) {
		Integer totalAmount = 0;

		for (Map.Entry<Integer, Integer> entry : denominationMap.entrySet()) {
			totalAmount += entry.getKey() * entry.getValue();
		}

		return totalAmount;
	}

	public static void main(String[] args) {
		ATM atm = new ATM();
		atm.nextStep();
	}

	public void nextStep() {
		try {
			System.out.println(
					"Enter the number according to the operation you want to perform \n1:Deposit \n2:Withdrawal \n3 or any other key:Exit ");
			Integer operation = scanner.nextInt();

			switch (operation) {
			case 1:
				deposit();
				break;
			case 2:
				withdraw();
				break;
			case 3:
				System.exit(0);
			default:
				System.exit(0);
			}
		} catch (InputMismatchException e) {
			System.out.println("Invalid Input");
			System.exit(0);
		} catch (Exception e) {
			System.out.println("Some error occured");
		}

	}

	private class MyException extends RuntimeException {

		public MyException(String message) {
			super(message);
			System.out.println(message);
			nextStep();
		}

	}

}
