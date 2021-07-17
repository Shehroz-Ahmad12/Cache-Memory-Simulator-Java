package project;


import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList; 
import java.util.Queue; 

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Cache {
	
	private int cachesize;
	private int blocksize=16,hit=0 ,miss=0;
	private int blockNum;
	private int setNum;
	private String association;
	ArrayList<String> address;
	ArrayList<String>[] LRU;
	Queue<String>[] FIFO;
	private String replace;
	private String[][] cache;
	private int[] validbits;
	private static double[][] fiforatio = new double[4][3];
	private static double[][] lruratio = new double[4][3];
	private int[] setmax;
	private int totalset;
	
	public Cache(int a,String b,String c) {
		address = new ArrayList<String>();	
		cachesize=a;
		this.association=b;
		this.replace=c;
		switch(b) {
		case "Direct Map":
			switch(a) {
			case 1024:
				blockNum=64;
				break;
			case 2048:
				blockNum=128;
				break;
			case 4096:
				blockNum=256;
				break;
			default:
			}
			cache= new String[blockNum][blocksize];
			break;
		case "2 way":
			
			switch(a) {
			case 1024:
				blockNum=64;
				setNum=32;
				break;
			case 2048:
				blockNum=128;
				setNum=64;
				break;
			case 4096:
				blockNum=256;
				setNum=128;
				break;
			default:
			}
			
			cache= new String[setNum][blocksize];
			setmax = new int[setNum];
			totalset=2;
			break;
		case "4 way":
			switch(a) {
			case 1024:
				blockNum=64;
				setNum=16;
				break;
			case 2048:
				blockNum=128;
				setNum=32;
				break;
			case 4096:
				blockNum=256;
				setNum=64;
				break;
			default:
				
			}
			cache= new String[setNum][blocksize];
			setmax = new int[setNum];
			totalset=4;
			break;
		case "8 way":
			switch(a) {
			case 1024:
				blockNum=64;
				setNum=8;
				break;
			case 2048:
				blockNum=128;
				setNum=16;
				break;
			case 4096:
				blockNum=256;
				setNum=32;
				break;
			default:
				
			}
			cache= new String[setNum][blocksize];
			setmax = new int[setNum];
			totalset=8;
			break;
		default:
		}
		
		validbits = new int[blockNum];
		if(c.equals("LRU")){
			LRU = new ArrayList[setNum];
			for(int i=0;i<setNum;i++)
				LRU[i]= new ArrayList<String>();
		}else if(c.equals("FIFO")){
			FIFO = new Queue[setNum];
			for(int i=0;i<setNum;i++)
				FIFO[i] = new LinkedList<>();
		}
	}

	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		System.out.print("Give Filename e.g address.txt: ");
		String name = in.next();
		System.out.println();
		Cache[] cache = new Cache[21];
		int[] size = {1024,2048,4096};
		for(int i=0;i<3;i++) {
			cache[i] = new Cache(size[i],"Direct Map","");
			cache[i].work1(name);
		}
		System.out.println("Direct Mapped Calculated");
		for(int i=3;i<6;i++) {
			cache[i] = new Cache(size[i-3],"2 way","FIFO");
			cache[i].work2(name);
		}
		for(int i=6;i<9;i++) {
			cache[i] = new Cache(size[i-6],"2 way","LRU");
			cache[i].work2(name);
		}
		System.out.println("2-way Calculated");
		for(int i=9;i<12;i++) {
			cache[i] = new Cache(size[i-9],"4 way","FIFO");
			cache[i].work4(name);
		}
		for(int i=12;i<15;i++) {
			cache[i] = new Cache(size[i-12],"4 way","LRU");
			cache[i].work4(name);
		}
		System.out.println("4-way Calculated");
		for(int i=15;i<18;i++) {
			cache[i] = new Cache(size[i-15],"8 way","FIFO");
			cache[i].work8(name);
		}
		for(int i=18;i<21;i++) {
			cache[i] = new Cache(size[i-18],"8 way","LRU");
			cache[i].work8(name);
		}
		System.out.println("8-way Calculated");
		System.out.println("\n");
		
		String[] type= {"Direct","2 way","4 way","8 way"};
		System.out.println("\t  FIFO Table");
		System.out.println("\t1024    2048    4096");
		for(int i=0;i<fiforatio.length;i++) {
			System.out.print(type[i]+" ");
			for(int j=0;j<fiforatio[0].length;j++) {
				if(j==0)
					System.out.print("\t"+fiforatio[i][j]+" ");
				else
					System.out.print(fiforatio[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("\t  LRU Table");
		System.out.println("\t1024    2048    4096");
		for(int i=0;i<lruratio.length;i++) {
			System.out.print(type[i]+" ");
			for(int j=0;j<lruratio[0].length;j++) {
				if(j==0)
					System.out.print("\t"+fiforatio[i][j]+" ");
				else
					System.out.print(lruratio[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	public void work1(String name) {
		this.readaddress(name);
		for(int p=0;p<this.address.size();p++)
			this.directcache(address.get(p));
		
		Cache.missratio(this.miss, this.cachesize, this.association, this.replace);
	}
	
	public void work2(String name) {
		this.readaddress(name);
		for(int p=0;p<this.address.size();p++)
			this.twoway(address.get(p));
		Cache.missratio(this.miss, this.cachesize, this.association, this.replace);
	}

	public void work4(String name) {
		this.readaddress(name);
		for(int p=0;p<this.address.size();p++)
			this.fourway(address.get(p));
		
		Cache.missratio(this.miss, this.cachesize, this.association, this.replace);
	}
	
	public void work8(String name) {
		this.readaddress(name);
		for(int p=0;p<this.address.size();p++)
			this.eigthway(address.get(p));
		
		Cache.missratio(this.miss, this.cachesize, this.association, this.replace);
	}
	//address, divide in fields, check valid bit of its block no. if 0 then copy block no. to the block,
	//if 1 then check its tag (of block no.) in tag array w/ tag of current address being checked.
	//if tag matches then its a hit. at the offset of block the desired data will be present
	//its a miss if tag mismatches, copy whole block into cache
	public void directcache(String address) {
			String binaddress,offset,block = null,tag = null;
			int decimaloffset,decimalblock,len;
			
			binaddress = bintoHex(address);
			len=binaddress.length()-4;
			offset = binaddress.substring(len);
			
			if(this.blockNum == 64) {
				block = binaddress.substring(binaddress.length()-10,len);
				tag = binaddress.substring(0,binaddress.length()-10);
			}
			else if(this.blockNum == 128) {
				block = binaddress.substring(binaddress.length()-11,len);
				tag = binaddress.substring(0,binaddress.length()-11);
			}
			else if(this.blockNum == 256) {
				block = binaddress.substring(binaddress.length()-12,len);
				tag = binaddress.substring(0,binaddress.length()-12);
			}
			decimaloffset=Integer.parseInt(offset,2);
			decimalblock=Integer.parseInt(block,2);
			
			decimalblock=directmap(decimalblock);
			
			if(validbits[decimalblock]==0) {
				miss++;
				//copy block
				this.copydirectblock(decimalblock, tag);
				validbits[decimalblock]=1;
			}
			else if(cache[decimalblock][decimaloffset].equals(tag)) 
				hit++;
			else {
				miss++;
				//copy block
				this.copydirectblock(decimalblock, tag);
			}
		}
		
	public void copydirectblock(int decimalblock, String tag) {
			for(int i=0;i<this.blocksize;i++)
				cache[decimalblock][i]=tag;
		}
	
	public int directmap(int decblock) {
		if(decblock>this.blockNum)
			decblock = decblock%this.blockNum;
		return decblock;
	}
	
	
	public String bintoHex(String s) {
		int dec = Integer.parseInt(s,16);
		return Integer.toBinaryString(dec);
	}
	
	public void readaddress(String file) {
		File k = new File(file);
		try {
		BufferedReader read = new BufferedReader(new FileReader(k));
		String line = read.readLine();
		while(line!=null)
		{
			this.address.add(line);
			line = read.readLine();
		}
		
		Collections.sort(this.address);
		read.close();
		
		}catch(IOException e) {
			
		}
	} 

	//address, divide in fields (tag, set, offset) check valid bit, go to a set using set bits, check tag of each block, 
		//if matches with a block then hit otherwise miss.
		//for mapping, find block no.%set no. to find the set in which the block will be mapped. inside the set add the block in any place.
		//for ease map to first block then second and onwards. when a set is full, use replacement policy for that set
		
		//In fifo, add block no. to queue as they add to cache. when a set is full, remove a block  no. from queue and replace that block with
		//incoming new block. Would need separate queues for each set.
		
		//In lru, add block no. again if its reused. replace the block no. at first index of the lru list. Separate lists for sets
		
	public void twoway(String address) {
			String binadd = null,offset,set = null,tag=null;
			int decimaloffset = 0,decimalset = 0,len;
			
			binadd = bintoHex(address);
			len = binadd.length()-4;
			offset = binadd.substring(len);
				
				if(this.setNum == 32) {
					set = binadd.substring(binadd.length()-9,len);
					tag = binadd.substring(0,binadd.length()-9);
				}
				else if(this.setNum == 64) {
					set = binadd.substring(binadd.length()-10,len);
					tag = binadd.substring(0,binadd.length()-10);
				}
				else if(this.setNum == 128) {
					set = binadd.substring(binadd.length()-11,len);
					tag = binadd.substring(0,binadd.length()-11);
				}
				decimaloffset=Integer.parseInt(offset,2);
				decimalset=Integer.parseInt(set,2);
				
				
				decimalset=setmap(decimalset);
				if(validbits[decimalset]==0) {
					miss++;
					//copy block
					copysetblock(decimalset,tag);
					validbits[decimalset]=1;
				}
				else if(cache[decimalset][decimaloffset].equals(tag)) { 
					hit++;
					if(this.replace.equals("LRU"))
						lru(decimalset,tag);
				}
				else {
					miss++;
					//copy block
					copysetblock(decimalset,tag);
					
				}
		}
		
	public void fourway(String address) {
			String binadd = null,offset,set = null,tag=null;
			int decimaloffset = 0,decimalset = 0,len;
			
			binadd = bintoHex(address);
			len = binadd.length()-4;
			offset = binadd.substring(len);
				
				if(this.setNum == 16) {
					set = binadd.substring(binadd.length()-8,len);
					tag = binadd.substring(0,binadd.length()-8);
				}
				else if(this.setNum == 32) {
					set = binadd.substring(binadd.length()-9,len);
					tag = binadd.substring(0,binadd.length()-9);
				}
				else if(this.setNum == 64) {
					set = binadd.substring(binadd.length()-10,len);
					tag = binadd.substring(0,binadd.length()-10);
				}
				decimaloffset=Integer.parseInt(offset,2);
				decimalset=Integer.parseInt(set,2);
				
				
				decimalset=setmap(decimalset);
				if(validbits[decimalset]==0) {
					miss++;
					//copy block
					copysetblock(decimalset,tag);
					validbits[decimalset]=1;
				}
				else if(cache[decimalset][decimaloffset].equals(tag)) { 
					hit++;
					if(this.replace.equals("LRU"))
						lru(decimalset,tag);
				}
				else {
					miss++;
					//copy block
					copysetblock(decimalset,tag);
					
				}
		}
		
	public void eigthway(String address) {
			String binadd = null,offset,set = null,tag=null;
			int decimaloffset = 0,decimalset = 0,len;
			
			binadd = bintoHex(address);
			len = binadd.length()-4;
			offset = binadd.substring(len);
				
				if(this.setNum == 8) {
					set = binadd.substring(binadd.length()-7,len);
					tag = binadd.substring(0,binadd.length()-7);
				}
				else if(this.setNum == 16) {
					set = binadd.substring(binadd.length()-8,len);
					tag = binadd.substring(0,binadd.length()-8);
				}
				else if(this.setNum == 32) {
					set = binadd.substring(binadd.length()-9,len);
					tag = binadd.substring(0,binadd.length()-9);
				}
				decimaloffset=Integer.parseInt(offset,2);
				decimalset=Integer.parseInt(set,2);
				
				
				decimalset=setmap(decimalset);
				if(validbits[decimalset]==0) {
					miss++;
					//copy block
					copysetblock(decimalset,tag);
					validbits[decimalset]=1;
				}
				else if(cache[decimalset][decimaloffset].equals(tag)) { 
					hit++;
					if(this.replace.equals("LRU"))
						lru(decimalset,tag);
				}
				else {
					miss++;
					//copy block
					copysetblock(decimalset,tag);
					
				}
		}
		
	public String lru(int set,String tag) {
			String old;
			if(LRU[set].size()==totalset && LRU[set].indexOf(tag)==-1) {
				old = LRU[set].remove(0);
				LRU[set].add(tag);
			}
			else if(LRU[set].indexOf(tag)==-1) {
				old = tag;
				LRU[set].add(tag);
			}
			else{ //if already exists, replace its occurence
				LRU[set].remove(tag);
				old = tag;
				LRU[set].add(tag);
			}
			return old;
		}
		
	public void copysetblock(int set, String tag) {
			String oldtag=null;
			if(setmax[set]==totalset) {
				//replacement
				if(this.replace.equals("LRU"))
					oldtag = lru(set,tag);
				else {
					oldtag = FIFO[set].remove();
					FIFO[set].add(tag);
				}
				if(!oldtag.equals(tag))
					for(int i=0;i<cache[set].length;i++)
						if(cache[set][i].equals(oldtag)) {
							for(int j=i;j<i+blocksize;j++)
								cache[set][j]=tag;
							break;
					}
			}
			//if set full, replacement takes place, put the block in that space
			else {
				for(int i=0;i<cache[set].length;i++)
					if(cache[set][i]==null || cache[set][i].isEmpty()) {
						for(int j=i;j<i+blocksize;j++)
							cache[set][j]=tag;
						break;
					}
				setmax[set]++;
				if(this.replace.equals("LRU"))
					LRU[set].add(tag);
				else
					FIFO[set].add(tag);
						//find empty space in set and copy whole block there
						// increment number of blocks in set in setmax array.
			}
		}
		
	public int setmap(int block) {
			block = block%this.setNum;
			return block;
		}
	
	public static void missratio(int miss, int size, String type, String replace) {
		double missratio=miss/1000d;
		int i=0,j=0;
			switch(size) {
			case 1024:
				j=0;
				break;
			case 2048:
				j=1;
				break;
			case 4096:
				j=2;
				break;
			default:
				
			}

			switch(type) {
			case "Direct Map":
				i=0;
				break;
			case "2 way":
				i=1;
				break;
			case "4 way":
				i=2;
				break;
			case "8 way":
				i=3;
				break;
			default:
				break;
			}
			
			if(replace.equals("FIFO"))
				fiforatio[i][j]=missratio;
			else if(replace.equals("LRU"))
				lruratio[i][j]=missratio;
			else {
				fiforatio[i][j]=missratio;
				lruratio[i][j]=missratio;
			}
	}

}