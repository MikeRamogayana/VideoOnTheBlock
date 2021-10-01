package model;

import datastructure.Node;
import datastructure.SList;

public class Blockchain extends SList<Block> {
	
	/**
	 * Construct Blockchain given a list of blocks
	 * 
	 * @param blocks
	 */
	public Blockchain(Block[] blocks) {
		insert(createBlock());
		for(Block block: blocks) {
			insert(block);
		}
	}

	/**
	 * Default Constructor
	 */
	public Blockchain() {
		insert(createBlock());
	}
	
	public Block createBlock() {
		Block block = new Block();
		block.setId(this.size + 1);
		return block;
	}
	
	/**
	 * Validates and Add Block The Chain
	 * 
	 * @param block
	 * @return String "Valid" if block is added successfully, else
	 * String "Invalid"
	 */
	public void addValidatedBlock(Block block) {
		for(Block b: this) {
			if(b.getHash().equals(block.getHash())) {
				return;
			}
		}
		insert(block);
	}
	
	/**
	 * Check if the hashes of the previous block in the new block and the one in
	 * the previous block matches
	 * 
	 * @param block
	 * @return true if matched, else false
	 */
	private boolean isValid(Block block) {
		if(this.size < 1) {
			return true;
		}
		if(block.getPrevHash().equals(head.getElement().getHash())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Validates the Private and Public Keys of The Smart Contract
	 * 
	 * @param block
	 * @param client
	 * @return
	 */
	public boolean validateBlock(Block block, Client client) {
		if(block.getPublicKey().equals(client.getPublicKey())) {
			if(block.getDataFile().getPrivateKey().equals(client.getPrivateKey())) {
				return isValid(block);
			}
		}
		return false;
		
	}
	
	public int getSize() {
		return size;
	}
	
	public Node<Block> getLastBlock() {
		return head;
	}

	public Block createBlock(String publicKey, Video df, double timeWatched) {
		if(this.size < 1) {
			createBlock();
		}
		return new Block(this.getSize()+1, head.getElement().getHash(), publicKey, df, timeWatched);
	}

}
