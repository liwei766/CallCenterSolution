/**
 * Copyright (C) 2017 OPTiM Corp. All rights reserved.
 * システム名：
 * ソースファイル名：OptimalBizService.java
 * 概要：
 *
 * 修正履歴：
 *   編集者		日付					概要
 *
 */
package cloud.optim.callcentersolution.core.modules.xauth;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpStatus;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cloud.optim.callcentersolution.core.modules.xauth.HttpClientWrapper.ResponseData;

public class OptimalBizService extends XAuth{

    /** コンストラクタ */
	public OptimalBizService(String url, String method, String xAuthConsumerToken, String xAuthConsumerSecret, String xAuthToken, String xAuthTokenKey) {

		this.url  = url;
		this.method = method;

		this.oAuthConsumerToken = xAuthConsumerToken;
		this.oAuthConsumerSecret = xAuthConsumerSecret;

		this.oAuthToken = xAuthToken;
		this.oAuthTokenSecret = xAuthTokenKey;
	}

    /** コンストラクタ For Password Update */
	public OptimalBizService(String url, String method, String xAuthConsumerToken, String xAuthConsumerSecret, String xAuthToken, String xAuthTokenKey, String xmldata) {

		this.url  = url;
		this.method = method;

		this.oAuthConsumerToken = xAuthConsumerToken;
		this.oAuthConsumerSecret = xAuthConsumerSecret;

		this.oAuthToken = xAuthToken;
		this.oAuthTokenSecret = xAuthTokenKey;

		this.xmlString = xmldata;
	}

	private String companyGuid;

	private String pageSize;
	private String pageNo;
	private String total;
	private ArrayList<OptimalBizUserInfo> userList;

    /**
     * Get Auth token
     */
    public String getOAuthToken() {
        return this.oAuthToken;
    }

    /**
     * Get Auth token Secret
     */
    public String getOAuthTokenSecret() {
        return this.oAuthTokenSecret;
    }

    /**
     * Get companyGuid
     */
    public String getOptimalBizCompanyGuid() {
        return this.companyGuid;
    }

    /**
     * Get pageSize
     */
    public String getPageSize() {
        return this.pageSize;
    }

    /**
     * Get pageNo
     */
    public String getPageNo() {
        return this.pageNo;
    }

    /**
     * Get total
     */
    public String getTotal() {
        return this.total;
    }

    /**
     * Get userList
     */
    public ArrayList<OptimalBizUserInfo> getUserList() {
        return this.userList;
    }

    /**
     * Post OptimalBiz token Info
     */
	public int getOptimalBizTokenInfo() {
		int responseStatus = 0;
		ResponseData responseData = null;

		responseData = request();
		responseStatus = responseData.status;

		//HTTPステータス:200
		if (responseStatus == HttpStatus.SC_OK){
			dissloveTokenString(responseData.entityBody.toString());
		}

		return responseStatus;
	}

    /**
     * Disslove Response Token String
     */
	private void dissloveTokenString (String str) {

		if (str != null && str.length() != 0) {

	        int len = str.length();

	        int i= str.indexOf(OAuthTokenKey);
	        int j= str.indexOf(OAuthTokenSecretKey);

	        this.oAuthToken = str.substring(i + OAuthTokenKey.length() + 1, j-1);
	        this.oAuthTokenSecret = str.substring(j  +OAuthTokenSecretKey.length() + 1);
		}
	}

    /**
     * Get OptimalBiz Company Info Request
     */
	public int getOptimalBizCompanyInfo() {

		ResponseData responseData = null;
		responseData = request();
		int responseStatus = responseData.status;

		//HTTPステータス:200
		if (responseStatus == HttpStatus.SC_OK){
			String strXml = responseData.entityBody.toString();
			parseXmlForCompanyInfo(strXml);
		}

		return responseStatus;
	}

    /**
     * Set CompanyGuid
     * @param strXml
     * @return companyGuid
     */
	private void parseXmlForCompanyInfo(String strXml) {

		Document document = null;

		try {
	    	document = DocumentHelper.parseText(strXml);
    	} catch (DocumentException e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
    	 }

    	Element root = document.getRootElement();

//        String  companyId=  root.elementText("code");
//        String  companyName=  root.elementText("name");

        String  companyGuid=  root.elementText("guid");

        this.companyGuid = companyGuid;
	}

    /**
     * Get OptimalBiz UserList Info
     */
	public int getOptimalUserListInfo() {

		ResponseData responseData = null;
		responseData = request();

		int responseStatus = responseData.status;

		//HTTPステータス:200
		if (responseStatus == HttpStatus.SC_OK){
			String strXml = responseData.entityBody;
			parseXmlForUserList(strXml);
		}

		return responseStatus;
	}

    /**
     * set UserList
     * @param strXml
     */
	private void parseXmlForUserList(String strXml) {

        Document doc = null;
        if(this.userList != null) {
        	this.userList.clear();
        } else {
        	this.userList = new ArrayList<OptimalBizUserInfo>();
        }

        try {
            doc = DocumentHelper.parseText(strXml);

            Element rootElt = doc.getRootElement();

	        this.pageSize = rootElt.attributeValue("per");
	        this.pageNo = rootElt.attributeValue("page");
	        this.total=  rootElt.attributeValue("total");

            Iterator iter = rootElt.elementIterator("user");

            while (iter.hasNext()) {

                Element recordEle = (Element) iter.next();
                String guid = recordEle.elementTextTrim("guid");
                String name = recordEle.elementTextTrim("name");

            	OptimalBizUserInfo user = new OptimalBizUserInfo();
            	user.setUserGuid(guid);
            	user.setUserName(name);
            	this.userList.add(user);
            }

        } catch(DocumentException e) {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
        }
	}

    /**
     * Put Update OptimalBiz UserPassword
     */
	public int updateOptimalBizUserPassword() {

		ResponseData responseData = null;
		responseData = request();

		int responseStatus = responseData.status;

		//HTTPステータス:204
//		if (responseStatus == HttpStatus.SC_NO_CONTENT){
//			System.out.println("User Update Success!!!");
//		}
		return responseStatus;
	}
}
