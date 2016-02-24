package com.babeeta.butterfly.application.management.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.babeeta.butterfly.application.management.entity.BadWords;

/***
 * 敏感词工具类
 * 
 * @author zeyong.xia
 * @date 2011-8-31
 * @time 下午03:17:53
 * @group babeeta
 */
public class WordsUtil {

	public int maxWordLength = 0;

	public int minWordLength = 0;

	public  HashMap<String, Integer> hash = new HashMap<String, Integer>();

	public  boolean[] fastCheck = new boolean[Character.MAX_VALUE];

	public  boolean[] charCheck = new boolean[Character.MAX_VALUE];

	public WordsUtil(List<BadWords> badwords) {
		for (BadWords word : badwords) {
			if (!hash.containsKey(word.getWord())) {
				hash.put(word.getWord(), null);
				maxWordLength = Math.max(maxWordLength, word.getWord().length());
				fastCheck[word.getWord().toCharArray()[0]] = true;
				char[] ch = word.getWord().toCharArray();
				for (char c : ch) {
					charCheck[c] = true;
				}
			}
		}
	}

	/***
	 * 通过正则匹配掉src的符号
	 * 
	 * @param src
	 * @param regex
	 * @return
	 */
	public boolean filterWord(String text) {
		int index = 0;
		//int offset;
		//char[] c = text.toCharArray();
		int num = 0;
		while (index < text.length()) {
			// System.out.println(c[index]);
			// 匹配脏字字符
			if (!fastCheck[text.toCharArray()[index]]) {
				while (index < text.length() - 1
						&& !fastCheck[text.toCharArray()[++index]])
					;
			}

			for (int j = 1; j <= Math.min(maxWordLength, text.length() - index); j++) {
				num++;
				if (!charCheck[text.toCharArray()[index + j - 1]]) {
					break;
				}

				String sub = text.substring(index, index + j);

				if (hash.containsKey(sub)) {
					System.out.println("sub= " + sub + " num=" + num);
					return true;
				}
			}

			index++;
		}
		return false;
	}

	public static void main(String[] args) {
		String str = "中国~!很多@就#就￥ 广东$季候风%会计法^很多&季候风*胡椒粉(胡椒粉)更好的-回复——会更大+回家=各分店??？广东大概:更好的";
		str = str.replaceAll(
				"[~,!,@,#,$,%,^,&,\\*,\\(,\\),\\-,——,\\+,=,;,:,\\?]", "");
		System.out.println(str);
		List<BadWords> list=new ArrayList<BadWords>();
		for(int i=0;i<10000;i++)
		{
			BadWords w=new BadWords();
			w.setId(String.valueOf(i));
			if(i%3==0)
			{
				w.setWord("打倒共产党");
			}
			else
			{
				w.setWord("法轮功");
			}
			list.add(w);
		}
		//String strs="打倒共产有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，法轮功只不过不能发现一有必要创建一个临时的替换的代码就不贴了，跟判断包含类似，只不过不能发现一打倒共产党";
	    String strs="我是毛泽东";
		strs=strs.replace("法轮功", "***");
		System.out.println("strs length= "+strs.length());
	    long star=System.currentTimeMillis();
	    WordsUtil u=new WordsUtil(list);
	    System.out.println(u.filterWord(strs));
	    System.out.println("耗时"+(System.currentTimeMillis()-star)+"毫秒");
	}
}
