package com.sinjon.ai;

import javax.swing.CellEditor;

/**
 * ν���߼�
 * 
 * @author sinjon
 *
 */
public class PredicateLogic {

	public static String predicateTextPremise = "(@x){($y)[S(x,y)^M(y)]��($z)[I(z)^E(x,z)]}";// ǰ��
	public static String predicateTextConclusion = "[~($z)I(z)]��{(@x)(@y)[M(y)��~S(x,y)]}";// ����

	/**
	 * ������
	 * 
	 * @param args0
	 */
	public static void main(String[] args) {
		// PredicateLogic predicateLogic = new PredicateLogic();
		System.out.println("�����̺�:");
		predicateTextPremise = EliminateContains(predicateTextPremise);
		predicateTextConclusion = EliminateContains(predicateTextConclusion);
		System.out.println();

		System.out.println("��С�񶨷��ŵ�Ͻ��:");
		predicateTextPremise = shrinkNotSign(predicateTextPremise);
		predicateTextConclusion = shrinkNotSign(predicateTextConclusion);
		System.out.println();

		System.out.println("�Ա������л���(����û����):");
		System.out.println("�������ʵ�ֵ���Ʋ���ȥ:");
		predicateTextPremise = TransiformPrenexNormalForm(predicateTextPremise);
		predicateTextConclusion = TransiformPrenexNormalForm(predicateTextConclusion);
		System.out.println();

		System.out.println("��Ϊ��ȡ��ʽ������Ӿ伯:");
		predicateTextPremise = getClauseSet(predicateTextPremise);
		predicateTextConclusion = getClauseSet(predicateTextConclusion);
		System.out.println();

		System.out.println("�ϲ��Ӿ伯(�����Ӿ伯Ҫȡ��):");
		String clauseSet = mergeClauseSet();// ����Ӿ伯
		System.out.println();

		System.out.println("���Ӿ伯���й��:");
		resolution(clauseSet);//���

	}

	/**
	 * ���
	 * 
	 * @param text
	 */
	private static void resolution(String text) {
		text = text.replace("{", "");
		text = text.replace("}", "");
		String[] clauseSets = text.split("\\),");
		for (int i = 0; i < clauseSets.length - 1; i++) {
			clauseSets[i] = clauseSets[i] + ")";

		}

		for (int i = 0; i < clauseSets.length - 1; i++) {
			String tempStr1 = clauseSets[i];

			for (int j = i + 1; j < clauseSets.length; j++) {
				String tempStr2 = clauseSets[j];
				tempStr2 = "~" + tempStr2;
				tempStr2.replace("~~", "");
				if (tempStr1.contains(tempStr2)) {
					int indexOf = tempStr1.indexOf(tempStr2);
					tempStr1 = tempStr1.substring(0, indexOf)
							+ tempStr1.substring(indexOf + tempStr2.length(), tempStr1.length());
					tempStr1 = tempStr1.replace("vv", "v");
					if (tempStr1.charAt(0) == 'v') {
						tempStr1 = tempStr1.substring(1);
					}
					clauseSets[i] = tempStr1;

				}
			}

		}
		System.out.println("�������̽����");
		for (int i = 0; i < clauseSets.length; i++) {

			System.out.println(clauseSets[i]);
		}
		System.out.println();

		boolean flag = false;
		System.out.println("������ս��");
		out: for (int i = 0; i < clauseSets.length - 1; i++) {
			String tempStr1 = clauseSets[i];

			for (int j = i + 1; j < clauseSets.length; j++) {
				String tempStr2 = clauseSets[j];
				tempStr2 = "~" + tempStr2;
				tempStr2 = tempStr2.replace("~~", "");

				if (tempStr1.substring(0, 2).equals(tempStr2.substring(0, 2))
						&& (tempStr1.contains("f(") || tempStr2.contains("f("))) {

					flag = true;
					System.out.println(clauseSets[i] + "��" + clauseSets[j] + "�����Ϊ��");
					break out;
				}
			}

		}
		if (!flag) {
			System.out.println("�������Ϊ��");
		}

	}

	/**
	 * 
	 * ν���߼������̺�
	 * 
	 * @param Text
	 *
	 * @return
	 */
	public static String EliminateContains(String Text) {
		String[] splitText = Text.split("��");
		boolean isFistAddErrorChar = true;
		int flag = 1;// ��־,��ǰ�ǵڼ���text
		String res = "";
		for (String text : splitText) {
			// ���ַ�������
			text = new StringBuffer(text).reverse().toString();

			int count = 0;// ��־
			String tempStr = "";
			for (char c : text.toCharArray()) {

				if (c == '}' || c == ']') {
					count++;
				} else if (c == '{' || c == '[') {
					count--;
				}

				if (count < 0 && isFistAddErrorChar) {

					tempStr = c + "~" + tempStr;
					count++;
					isFistAddErrorChar = false;
				} else {

					tempStr = c + tempStr;

				}

			} // for end char c
			if (flag == 1 && isFistAddErrorChar) {
				if (tempStr.charAt(0) == '[' || tempStr.charAt(0) == '(' || tempStr.charAt(0) == '{') {
					res = "~" + tempStr + "v";
				} else {
					res = "~" + "(" + tempStr + ")" + "v";
				}

			} else if (flag != splitText.length) {
				res = res + tempStr + "v";
			} else {
				res = res + tempStr;
			}

			flag++;
		}

		System.out.println(res);
		return res;

	}

	/**
	 * ��С��Ͻ��
	 * 
	 * @param text
	 * @return
	 */
	private static String shrinkNotSign(String text) {
		String res = text;
		boolean isFistAddErrorChar = true;
		// ������
		for (int i = 0; i < 26; i++) {
			char c = (char) ('a' + i);
			res = res.replace("~($" + c + ")", "(@" + c + ")~");
			res = res.replace("~(@" + c + ")", "($" + c + ")~");

		}

		int flag = 0;// ��־�ڼ�����ĸ
		String tempStr1 = "";
		// ��������
		for (char c1 : res.toCharArray()) {

			if (c1 == '~') {
				String tempSubStr = text.substring(flag + 1);
				int count = 0;// ��־
				String tempStr2 = "";
				for (char c2 : tempSubStr.toCharArray()) {
					flag = flag + 1;
					if (c2 == '}' || c2 == ']') {
						tempStr2 = tempStr2 + c2;
						count++;
						isFistAddErrorChar = false;
					} else if ((c2 == '{' || c2 == '[') && isFistAddErrorChar) {
						count--;
						tempStr2 = tempStr2 + c2 + '~';
					} else if ((c2 == 'v' || c2 == '^') && count < 0 && isFistAddErrorChar) {
						if (c2 == 'v') {
							c2 = '^';
						} else {
							c2 = 'v';
						}
						tempStr2 = tempStr2 + c2 + '~';
						count++;
						isFistAddErrorChar = false;
					} else {
						tempStr2 = tempStr2 + c2;
					}

				}

				tempStr1 = tempStr1 + tempStr2;
				break;

			} else {
				tempStr1 = tempStr1 + c1;
			}
			flag++;

		}

		res = tempStr1;
		res = res.replace("~~", "");
		System.out.println(res);
		return res;
	}

	/**
	 * ��Ϊǰ����ʽ
	 * 
	 * @param text
	 * @return
	 */
	private static String TransiformPrenexNormalForm(String text) {
		String res = text;
		String measureWords = "";

		int flag = 0;
		for (char c : text.toCharArray()) {

			if (c == '$') {

				res = res.replace("($" + text.charAt(flag + 1) + ")", "");
				measureWords = measureWords + "($" + text.charAt(flag + 1) + ")";
			} else if (c == '@') {
				res = res.replace("(@" + text.charAt(flag + 1) + ")", "");
				measureWords = measureWords + "(@" + text.charAt(flag + 1) + ")";
			}
			flag++;
		}

		if ((res.contains("v") && !res.contains("^")) || (!res.contains("v") && res.contains("^"))) {
			res = res.replace("[", "");
			res = res.replace("]", "");
			res = res.replace("{", "");
			res = res.replace("}", "");
		} else {
			res = res.replace("{", "");
			res = res.replace("}", "");
		}

		if (res.charAt(0) != '(' || res.charAt(0) != '[' || res.charAt(0) != '{') {
			res = '{' + res + '}';
		}

		System.out.println(measureWords + res);

		String[] enumString = { "f", "g", "h" };
		int enumIndex = 0;
		String replaceString = "";
		String[] splitText = measureWords.split("\\)\\(");
		splitText[0] = splitText[0].replace("(", "");
		splitText[splitText.length - 1] = splitText[splitText.length - 1].replace(")", "");

		for (int x = splitText.length - 1; x > 0; x--) {
			replaceString = "";
			String temp = splitText[x];
			char c = 0;
			if (temp.charAt(0) == '$') {
				c = temp.charAt(1);
				for (int y = 0; y < x; y++) {
					if (splitText[y].charAt(0) == '@') {
						replaceString = replaceString + "," + splitText[y].charAt(1);
					}
				}
				replaceString = enumString[enumIndex] + "(" + replaceString.substring(1) + ")";

				enumIndex++;

			}

			// �滻
			if (c != 0) {
				res = res.replace(c + "", replaceString);

			}
			res = res.replace("{", "");
			res = res.replace("}", "");

		}
		System.out.println("��ȥ����֮��" + res);

		return res;
	}

	/**
	 * ��Ϊ��ȡ��ʽ������Ӿ伯
	 * 
	 * @param text
	 * @return
	 */
	private static String getClauseSet(String text) {
		String res = text;
		int flag = 0;// ��־
		int count = 0;
		if (text.contains("^")) {
			for (char c1 : res.toCharArray()) {
				String tempRes = res.substring(0, flag + 1);

				count = 0;
				if (c1 == 'v') {
					String temp = res.substring(flag + 1);

					for (char c2 : temp.toCharArray()) {
						if (c2 == '}' || c2 == ']') {
							count++;
						} else if ((c2 == '{' || c2 == '[')) {
							count--;
						} else if (c2 == '^' && count < 0) {
							tempRes = res.substring(0, flag);
							tempRes = tempRes.replace("[", "");
							tempRes = tempRes.replace("]", "");
							temp = temp.replace("[", "");
							temp = temp.replace("]", "");
							String[] temp1 = temp.split("\\^");

							tempRes = "[" + tempRes + "v" + temp1[0] + "]" + "^" + "[" + tempRes + "v" + temp1[1] + "]";
							res = tempRes;
							System.out.println("��ȡ��ʽ��" + res);
							count++;
						}
					}

				}

				flag++;
			}
		} else {
			System.out.println("��ȡ��ʽ��" + res);
		}

		// ����ȡ��ʽ��Ϊ�Ӿ伯
		res = res.replace("^", ",");
		res = res.replace("[", "");
		res = res.replace("]", "");
		res = "{" + res + "}";
		System.out.println("�Ӿ伯��" + res);

		return res;
	}

	/**
	 * �ϲ��Ӿ伯
	 */
	private static String mergeClauseSet() {
		String res = predicateTextPremise;
		String temp = predicateTextConclusion;
		res = res.replace("{", "");
		res = res.replace("}", "");

		temp = temp.replace("{", "");
		temp = temp.replace("}", "");
		String[] tempStr = temp.split("v");
		temp = "";
		for (String str : tempStr) {
			temp = temp + "," + "~" + str;
		}
		temp = temp.substring(1);
		res = "{" + res + "," + temp + "}";
		res = res.replace("~~", "");
		System.out.println("�ϲ��Ӿ伯:" + res);
		return res;

	}

}
