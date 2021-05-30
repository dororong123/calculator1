package com.naver.BackwardOper.Core;

import java.util.Scanner;
import com.naver.BackwardOper.Operator.ABS_CalMember;
import com.naver.BackwardOper.Operator.ABS_Calculer;
import com.naver.BackwardOper.Operator.NumOperand;
import com.naver.BackwardOper.Stack.Stack;
import java.math.BigInteger;

public class BakcwardOper {
	public static void main(String[] args) throws ClassNotFoundException
	{
		System.out.println("후위연산 계산기.");
		Scanner scanner = new Scanner(System.in);
		String input;
		while(true)
		{
				Stack<ABS_CalMember> value = new Stack<ABS_CalMember>(3);
				System.out.print("\n식을 입력하세요: ");
				input = scanner.nextLine();
				try { postfixExp(input, value); }
				catch(Exception e)
				{
					e.printStackTrace();
					System.out.println("후위 표기 결과를 가져오던 중 오류가 발생하였습니다.");
					continue;
				}
				System.out.print("후위 표기 식입니다:");
				for(int i = 0; i < value.size(); i++)
				{// 후위 표기 스택에서 하나씩 출력.
					System.out.print(" " + value.getMemberAt(i));
				}
				try { System.out.println("\n연산 결과입니다: " + getValue(value));}
				catch(Exception e)
				{ 
					System.out.println("\n연산 과정중 오류가 발생하였습니다.");
					System.out.println("다시 입력하세요.");
					continue;
				}			
				System.out.println("계속하시겠습니까?(Y/N)");
				input = scanner.nextLine();
				if(input.equalsIgnoreCase("Y")) continue;
				else break;
		}
		System.out.println("프로그램을 종료합니다.");
		scanner.close();
	}
	private static Stack<ABS_CalMember> postfixExp(String str, Stack<ABS_CalMember> calStack)
	{
		Stack<ABS_CalMember> tempStack = new Stack<ABS_CalMember>(calStack.getSlotSize());
		BigInteger biginteger = new BigInteger("0");
		boolean numberTask = false;

		for(int i = 0; i < str.length(); i++)
		{
			char taskChar = str.charAt(i);
			if(taskChar >= '0' && taskChar <= '9')
			{
				if(!numberTask) biginteger = BigInteger.ZERO;
				biginteger = (biginteger.multiply(BigInteger.valueOf(10))).add(BigInteger.valueOf((taskChar - '0')));
				numberTask = true;
			}
			else if(ABS_CalMember.isExist(String.valueOf(taskChar)))
			{
				ABS_CalMember operator = ABS_CalMember.getInstance(String.valueOf(taskChar));
				if(numberTask)
				{
					calStack.pushBack(new NumOperand(biginteger));
					numberTask = false;
				}
				if(operator.toString().equals("("))
				{
					tempStack.pushBack(operator);
				}
				else if(operator.toString().equals(")"))
				{
					while(tempStack.size() >= 0)
					{
						if(tempStack.getBack().toString().equals("("))
						{
							tempStack.popBack();
							break;
						}
						calStack.pushBack(tempStack.getBack());
						tempStack.popBack();
					}
				}
				else if(operator instanceof ABS_Calculer)
				{
					ABS_Calculer calOper = (ABS_Calculer)operator;
					while(true)
					{
						if(tempStack.size() == 0)
						{
							tempStack.pushBack(operator);
							break;
						}
						ABS_Calculer calculer = tempStack.getBack() instanceof ABS_Calculer ?
								(ABS_Calculer)tempStack.getBack() : null;
						if(calculer == null || calculer.getPriority() < calOper.getPriority())
						{
							tempStack.pushBack(calOper);
							break;
						}
						calStack.pushBack(tempStack.getBack());
						tempStack.popBack();
					}
				}
			}
		}
		if(numberTask) calStack.pushBack(new NumOperand(biginteger));
		while(tempStack.size() > 0)
		{
			calStack.pushBack(tempStack.getBack());
			tempStack.popBack();
		}
		return calStack;
	}
	private static NumOperand getValue(Stack<ABS_CalMember> postfixStack)
	{
		Stack<NumOperand> tempNumStack = new Stack<NumOperand>(postfixStack.getSlotSize());
		for(int i = 0; i < postfixStack.size(); i++)
		{
			ABS_CalMember taskMember = postfixStack.getMemberAt(i);
			NumOperand x, y, number;
			ABS_Calculer calculer;
			if(taskMember instanceof NumOperand)
			{
				number = (NumOperand)taskMember;
				tempNumStack.pushBack(number);
			}
			else if(taskMember instanceof ABS_Calculer)
			{
				calculer = (ABS_Calculer)taskMember;
				x = tempNumStack.getBack(); tempNumStack.popBack();
				y = tempNumStack.getBack(); tempNumStack.popBack();
				tempNumStack.pushBack(calculer.task(y, x));
			}
		}
		if(tempNumStack.getMemberAt(0) == null) throw new NullPointerException();
		return tempNumStack.getMemberAt(0);
	}
}

