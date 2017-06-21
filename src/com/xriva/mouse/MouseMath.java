package com.xriva.mouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class MouseMath
{
	/*
	 * MOUSE - A Language for Microcomputers (interpreter) as described by Peter
	 * Grogono in July 1979 BYTE Magazine
	 * 
	 * Translated from C by Kevin J Gilhooly
	 */

	enum tagtype
	{
		MACRO, PARAM, LOOP
	};

	class frame
	{
		tagtype tag;
		int pos, off;
	};

	String program;

	char[] prog = new char[5000];
	int[] definitions = new int[26];
	int[] calstack = new int[20];
	int[] data = new int[260];
	int cal;
	int chpos;
	int level;
	int offset;
	int parnum;
	int parbal;
	int temp;
	frame[] stack = new frame[20];
	char ch;

	void pushcal(int datum)
	{
		calstack[cal++] = datum;
	}

	int popcal()
	{
		return calstack[--cal];
	}

	void push(tagtype tagval)
	{
		stack[level].tag = tagval;
		stack[level].pos = chpos;
		stack[level++].off = offset;
	}

	void pop()
	{
		chpos = stack[--level].pos;
		offset = stack[level].off;
	}

	void skip(char lch, char rch)
	{
		int cnt = 1;
		do
		{
			ch = prog[chpos++];
			if (ch == lch)
				cnt++;
			else if (ch == rch)
				cnt--;
		}
		while (cnt != 0);
	}

	public static void main(String[] args)
	{
		System.out.println("Mouse. Enter your statement.");
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		String program;
		try
		{
			program = console.readLine();
			MouseMath interpreter = new MouseMath();
			interpreter.interpret(program);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void interpret(String sourceCode)
	{
		prog = sourceCode.toCharArray();
		chpos = 0;
		level = 0;
		offset = 0;
		cal = 0;
		do
		{
			ch = prog[chpos++];
			switch (ch)
				{
				case ' ':
				case ']':
				case '$':
					break;
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					temp = 0;
					while (ch >= '0' && ch <= '9')
					{
						temp = 10 * temp + (ch - '0');
						ch = prog[chpos++];
					}
					pushcal(temp);
					chpos--;
					break;
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
				case 'G':
				case 'H':
				case 'I':
				case 'J':
				case 'L':
				case 'M':
				case 'N':
				case 'O':
				case 'P':
				case 'Q':
				case 'R':
				case 'S':
				case 'T':
				case 'U':
				case 'V':
				case 'W':
				case 'X':
				case 'Y':
				case 'Z':
					pushcal((ch - 'A') + offset);
					break;
				// case '?':
				// scanf("%d", &temp);
				// pushcal(temp);
				// break;
				case '!':
					System.out.printf("%d", popcal());
					break;
				case '+':
					pushcal(popcal() + popcal());
					break;
				case '-':
					pushcal(popcal() - popcal());
					break;
				case '*':
					pushcal(popcal() * popcal());
					break;
				case '/':
					pushcal(popcal() / popcal());
					break;
				case '.':
					pushcal(data[popcal()]);
					break;
				case '=':
					temp = popcal();
					data[popcal()] = temp;
					break;
				case '"':
					do
					{
						ch = prog[chpos++];
						if (ch == '!')
							System.out.println();
						else if (ch != '"')
							System.out.print(ch);
					}
					while (ch != '"');
					break;
				case '[':
					if (popcal() <= 0)
						skip('[', ']');
					break;
				case '(':
					push(tagtype.LOOP);
					break;
				case '^':
					if (popcal() <= 0)
					{
						pop();
						skip('(', ')');
					}
					break;
				case ')':
					chpos = stack[level - 1].pos;
					break;
				case '#':
					ch = prog[chpos++];
					if (definitions[(ch - 'A')] > 0)
					{
						push(tagtype.MACRO);
						chpos = definitions[(ch - 'A')];
						offset += 26;
					}
					else
						skip('#', ';');
					break;
				case '@':
				case '}':
					pop();
					skip('#', ';');
					break;
				case '%':
					ch = prog[chpos++];
					parnum = (ch - 'A');
					push(tagtype.PARAM);
					parbal = 1;
					temp = level - 1;
					do
					{
						temp--;
						switch (stack[temp].tag)
							{
							case MACRO:
								parbal--;
								break;
							case PARAM:
								parbal--;
								break;
							case LOOP:
								break;
							}
					}
					while (parbal != 0);
					chpos = stack[temp].pos;
					offset = stack[temp].off;
					do
					{
						ch = prog[chpos++];
						if (ch == '#')
						{
							skip('#', ';');
							ch = prog[chpos++];
						}
						if (ch == ',')
							parnum--;
					}
					while (parnum >= 0 && ch != ';');
					if (ch == ';')
						pop();
					break;
				case ',':
				case ';':
					pop();
					break;
				}
		}
		while (ch != '$');
	}
}
