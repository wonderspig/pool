import java.util.HashMap;
import java.util.Map;

public class trieTreeMap {
  
   public static	class TrieNode{
		 int nCount=0;
		 Map<Character,TrieNode> TrieNode;
		public TrieNode() {
			nCount = 1;
			TrieNode = new HashMap<Character, TrieNode>();
		}
		
	}
	
	public static void insert(TrieNode node,String word)
	{
		for(int i = 0;i<word.length();i++)
		{
			Character character =new Character(word.charAt(i));
			if(!node.TrieNode.containsKey(character))
			{
				node.TrieNode.put(character,new TrieNode());
			}else {
				node.TrieNode.get(character).nCount++;
			}
			node = node.TrieNode.get(character);
		}
	}
	public static int search(TrieNode node,String word)
	{
		for(int i = 0;i<word.length();i++)
		{
			Character character =new Character(word.charAt(i));
			if(!node.TrieNode.containsKey(character))
			{
				return 0;
			}else {
				node = node.TrieNode.get(character);
				
			}
			
		}
		return node.nCount;
	}
	static TrieNode trieNode = new TrieNode();
	
	public static void main(String[] args) {
		
		trieTreeMap tree = new trieTreeMap();

		String[] strs = { "banana", "软件", "bee", "absolute", "acm" };
		String[] prefix = { "网易", "软件", "band", "abc"};
		for (String s : strs) {
			tree.insert(trieNode, s);
			}
			// tree.printAllWords();
			for (String pre : prefix) {
			int num = tree.search(trieNode, pre);
			System.out.println(pre + " " + num);
			}
		 
	}

}
