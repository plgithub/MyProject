package cn.pipi.mobile.pipiplayer.util;

/**
 * RegexTool is used to regex the string ,such as : phone , qq , password , email .
 * 
 * @author ZHANGGeng
 * @version v1.0.1
 * @since JDK5.0
 *
 */

public class RegexTool {
	
	
	
	/**
	 * 
	 * @param phoneNum ����Ĳ��������һ���绰����ʱ�����ô˷���
	 * @return ���ƥ����ȷ��return true , else return else
	 */
	//���������ǵ绰���룬��Ե绰�����������ƥ��
	public static boolean regexPhoneNumber(String phoneNum){
		
		//�绰����ƥ����
		boolean isPhoneNum_matcher = phoneNum.matches("1[358]\\d{9}");
		//���isPhoneNum_matcher is true , ��return true , else  return false
		if(isPhoneNum_matcher)
			return true;
		return false;
	}
	
	/**
	 * 
	 * @param email ����Ĳ��������һ�������ַʱ�����ô˷���
	 * @return  ���ƥ����ȷ��return true , else return false
	 */
	//���������������ַ����������������ƥ��
	public static boolean regexEmailAddress(String email){
		
		//����ƥ����
		boolean isEmail_matcher = email.matches("[a-zA-Z_0-9]+@[a-zA-Z0-9]+(\\.[a-zA-Z]{2,}){1,3}");
		//���isEmail_matcher value is true , �� return true , else return false
		if(isEmail_matcher)
			return true;
					
		return false;
	}
	
	/**
	 * 
	 * @param phoneNum  ����ĵ绰����
	 * @param email     ����������ַ
	 * @return   ���ƥ����ȷ��return true , else return false
	 */
	public static boolean regexEmailAddressAndPhoneNum(String phoneNum , String email){
		
		//�绰����ƥ����
		boolean isPhoneNum_matcher = phoneNum.matches("1[358]\\d{9}");
		//����ƥ����
		boolean isEmail_matcher = email.matches("[a-zA-Z_0-9]+@[a-zA-Z0-9]+(\\.[a-zA-Z]{2,}){1,3}");
		
		//matcher value is true , �� return true , else  return false
		if(isEmail_matcher && isPhoneNum_matcher){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param qqNum �����QQ
	 * @return  ���ƥ����ȷ��return true�� else  return false
	 */
	public static boolean regexQQNumber(String qqNum){
		
		//QQ��ƥ����
		boolean isQQNum_matcher = qqNum.matches("[1-9]\\d{2,11}");
		
		if(isQQNum_matcher)
			return true;
		return false;
	}
	
	/**
	 * 
	 * @param pwd ������� ����
	 * @return ���ƥ����ȷ�������������return true�� else return false
	 */
	public static boolean regexPassWord(String pwd){
		
		//����ƥ����
		boolean isPassWord_matcher = pwd.matches("[0-9a-zA-Z_@$@]{6,12}");
		
		if(isPassWord_matcher)
			return true;
		
		return false;
	}
	
	
	
}
