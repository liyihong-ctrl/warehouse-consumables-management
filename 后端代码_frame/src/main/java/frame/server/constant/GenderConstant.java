package frame.server.constant;

import frame.server.constant.GenderConstant;

/**
 * 性别
 * 
 * @author Administrator
 *
 */
public class GenderConstant {

	private int TYPE;
	
	private String NAME;
	
	private GenderConstant(int type, String name) {
		this.TYPE = type;
		this.NAME = name;
	}
	
	public int getType() {
		return this.TYPE;
	}
	
	public String getName() {
		return this.NAME;
	}
	
	public static GenderConstant MALE = new GenderConstant(0, "男");
	
	public static GenderConstant FEMALE = new GenderConstant(1, "女");

}
