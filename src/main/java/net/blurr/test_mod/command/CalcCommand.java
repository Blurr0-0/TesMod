package net.blurr.test_mod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

import java.util.Stack;


public class CalcCommand {
	public CalcCommand(CommandDispatcher<CommandSourceStack> dispatcher) {

		dispatcher.register(Commands.literal("calc")
				.then(Commands.argument("expression", MessageArgument.message())
						.executes((command) -> {

							return (int) calc(command.getSource(), MessageArgument.getMessage(command, "expression").toString(), true);

						})));

	}

	private static double calc(CommandSourceStack pSource, String pExpression, boolean b) throws CommandSyntaxException {
		double pResult = evaluateExpression(pExpression, pSource.getLevel());
		pSource.sendSuccess(new TextComponent("result: " + pResult),true);

		return pResult;
	}

	private static double evaluateExpression(String expression, Level pLevel) {
		boolean negative = false;
		Stack<Double> values = new Stack<>();
		Stack<Character> operators = new Stack<>();

		for (int i = 0; i < expression.length(); i++) {
			char c = expression.charAt(i);

			if (c == ' ') {
				continue;
			}

			if (c == '-' && (i == 0 || (expression.charAt(i - 1) == '(' || (expression.charAt(i - 1) == ',' || isOperator(expression.charAt(i - 1)))))) {
				negative = true;
				continue;
			}

			if (c == '(') {
				operators.push(c);
			} else if (Character.isDigit(c)) {
				StringBuilder num = new StringBuilder();
				while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
					num.append(expression.charAt(i++));
				}
				i--;
				double value = Double.parseDouble(num.toString());
				if (negative) {
					value *= -1;
					negative = false;
				}
				values.push(value);
			} else if (isOperator(c)) {
				while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
					values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
				}
				operators.push(c);

			} else if (expression.startsWith("sin(", i)) {
				int j = Iargumentfunc(i, "sin(", expression);
				double Arg = evaluateExpression(expression.substring(i + 4, j), pLevel);
				values.push(Math.sin(Math.toRadians(Arg)));
				i = j;
			} else if (expression.startsWith("cos(", i)) {
				int j = Iargumentfunc(i, "cos(", expression);
				double Arg = evaluateExpression(expression.substring(i + 4, j), pLevel);
				values.push(Math.cos(Math.toRadians(Arg)));
				i = j;
			} else if (expression.startsWith("tan(", i)) {
				int j = Iargumentfunc(i, "tan(", expression);
				double Arg = evaluateExpression(expression.substring(i + 4, j), pLevel);
				values.push(Math.tan(Math.toRadians(Arg)));
				i = j;
			} else if (expression.startsWith("sqrt(", i)) {
				int[] args = IIargumentfunc(i, "sqrt(", expression);
				int j = args[0];
				int k = args[1];
				double a = evaluateExpression(expression.substring(i + 5, j), pLevel);
				double b = evaluateExpression(expression.substring(j, k), pLevel);
				values.push(Math.pow(a, 1 / b));
				i = k;
			} else if (expression.startsWith("pow(", i)) {
				int[] args = IIargumentfunc(i, "pow(", expression);
				int j = args[0];
				int k = args[1];
				double a = evaluateExpression(expression.substring(i + 4, j), pLevel);
				double b = evaluateExpression(expression.substring(j, k), pLevel);
				values.push(Math.pow(a, b));
				i = k;
			} else if (expression.startsWith("rand(", i)) {
				int[] args = IIargumentfunc(i, "rand(", expression);
				int j = args[0];
				int k = args[1];
				double a = evaluateExpression(expression.substring(i + 5, j), pLevel);
				double b = evaluateExpression(expression.substring(j, k), pLevel);
				values.push((Math.random()*(b-a))+a);
				i = k;
			} else if (expression.startsWith("score(", i)) {
				int[] args = IIargumentfunc(i, "score(", expression);
				int j = args[0];
				int k = args[1];
				String score = expression.substring(i + 6, j).replaceAll(" ", "");
				String name = expression.substring(j + 1, k).replaceAll(" ", "");
				double s = getScore(score, name, pLevel);
				values.push(s);
				i = k;
			} else if (c == ')') {
				while (operators.peek() != '(') {
					values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
				}
				operators.pop();
			}
		}

		while (!operators.isEmpty()) {
			values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
		}

		return values.pop();
	}

	private static int getScore(String score, String name, Level level) {
		Scoreboard scoreboard = level.getScoreboard();
		Objective obj = scoreboard.getObjective(score);
		int result = scoreboard.getOrCreatePlayerScore((name), obj).getScore();
		return result;
	}

	private static boolean isOperator(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
	}

	private static int precedence(char operator) {
		return switch (operator) {
			case '+', '-' -> 1;
			case '*', '/' -> 2;
			case '^' -> 3;
			default -> -1;
		};
	}

	private static double applyOperator(char operator, double b, double a) {
		return switch (operator) {
			case '+' -> a + b;
			case '-' -> a - b;
			case '*' -> a * b;
			case '/' -> a / b;
			case '^' -> Math.pow(a, b);
			default -> -1;
		};

	}

	private static int Iargumentfunc(int i, String token, String expression) {
		int j = i + token.length();
		int balance = 1;
		while (j < expression.length() && balance > 0) {
			char cur = expression.charAt(j);
			if (cur == '(') {
				balance++;
			} else if (cur == ')') {
				balance--;
			}
			j++;
		}

		return j - 1;
	}

	private static int[] IIargumentfunc(int i, String token, String expression) {
		int j = i + token.length();
		int balance = 1;
		boolean end = false;
		while (j < expression.length() && balance > 0 && !end) {
			char cur = expression.charAt(j);
			if (cur == '(') {
				balance++;
			} else if (cur == ')') {
				balance--;
			} else if (cur == ',' && balance == 1) {
				end = true;
			}
			j++;
		}
		j--;

		int k = j;
		while (k < expression.length() && balance > 0) {
			char cur = expression.charAt(k);
			if (cur == '(') {
				balance++;
			} else if (cur == ')') {
				balance--;
			}
			k++;
		}
		k--;

		return new int[]{j,k};
	}
}