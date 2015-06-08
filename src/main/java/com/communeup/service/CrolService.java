package com.communeup.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.communeup.crol.dao.NoticeDao;
import com.communeup.crol.dao.NoticeDaoImpl;
import com.communeup.crol.domain.Notice;
import com.communeup.crol.to.CrolInput;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class CrolService {

	private NoticeDao noticeDao = new NoticeDaoImpl();

	public Notice fetch( String noticeId ) {
		return noticeDao.getNotice(noticeId);
	}

	public void parseAndSave(CrolInput noticeInput) {
		// Parser Calls go here ...
		System.out.println("Calling parser with " + noticeInput.getAdditionalDescription());
		String parserOutout = parserCallTest(noticeInput.getAdditionalDescription());
		System.out.println("Parser Output : " + parserOutout);
		String output = createFreemarkerOutputFromInput(noticeInput, parserOutout);
		noticeDao.saveNotice(createNotice(noticeInput, output), noticeInput);
	}

	/**
	 * 
	 * @param jsonPayload
	 */
	private String createFreemarkerOutputFromInput(CrolInput input, String parserOutput) {
		Configuration config = new Configuration();

		try {
			config.setDirectoryForTemplateLoading(new File( "/home/ubuntu/crol-interface/src"));
			Template template = config.getTemplate("template.ftl");

			Map<String, Object> data = new HashMap<String, Object>();

			data.put("PIN", checkNullString(input.getPin()));
			data.put("Email", checkNullString(input.getEmail()));
			data.put("EndDate", checkNullString(input.getEndDate()));
			data.put("DueDate", checkNullString(input.getDueDate()));
			data.put("Printout", checkNullString(input.getPrintout()));
			data.put("StartDate", checkNullString(input.getStartDate()));
			data.put("OtherInfo", checkNullString(input.getOtherInfo()));
			data.put("RequestID", checkNullString(input.getRequestId()));
			data.put("SectionID", checkNullString(input.getSectionId()));
			data.put("OtherInfo", checkNullString(input.getOtherInfo()));
			data.put("ContactFax", checkNullString(input.getContactFax()));
			data.put("ShortTitle", checkNullString(input.getShortTitle()));
			data.put("AgencyName", checkNullString(input.getAgencyName()));
			data.put("AgencyCode", checkNullString(input.getAgencyCode()));
			data.put("VendorName", checkNullString(input.getVendorName()));
			data.put("SectionName", checkNullString(input.getSectionName()));
			data.put("ContactName", checkNullString(input.getContactName()));
			data.put("ContactPhone", checkNullString(input.getContactPhone()));
			data.put("VendorAddress", checkNullString(input.getVendorAddress()));
			data.put("ContractAmount", checkNullString(input.getContractAmount()));
			data.put("AddressToSubmit", checkNullString(input.getAddressToSubmit()));
			data.put("TypeOfNoticeCode", checkNullString(input.getTypeOfNoticeCode()));
			data.put("CategoryDescription", checkNullString(input.getCategoryDescription()));
			data.put("SelectionMethodCode", checkNullString(input.getSelectionMethodCode()));
			data.put("AdditionalDescription", checkNullString(input.getAdditionalDescription()));
			data.put("SpecialCaseReasonCode", checkNullString(input.getSpecialCaseReasonCode()));
			data.put("SelectionMethodDescription", checkNullString(input.getSelectionMethodDescription()));
			data.put("SpecialCaseReasonDescription", checkNullString(input.getSpecialCaseReasonDescription()));

			data.put("ParserOutput", parserOutput);

			StringWriter writer = new StringWriter();
			template.process(data, writer);
			writer.flush();

			return writer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}

		return null;
	}

	private Object checkNullString(String string) {
		return (string == null) ? "" : string;
	}

	private Notice createNotice(CrolInput noticeInput, String output) {
		Notice notice = new Notice();

		notice.setNoticeId(noticeInput.getRequestId());
		notice.setNoticeText(output);

		return notice;
	}

	public void parseAndUpdate(CrolInput noticeInput) {
		String parserOutout = parserCallTest(noticeInput.getAdditionalDescription());
		String output = createFreemarkerOutputFromInput(noticeInput, parserOutout);
		System.out.println(output);
		noticeDao.deleteNotice(createNotice(noticeInput, output));
	}

	public void delete(String noticeId) {
		noticeDao.deleteNotice(noticeId);
	}

	public List<Notice> fetchAfter(String timestamp) {
		System.out.println("Query by latest timestamp in service : " + timestamp);
		return noticeDao.getNoticeAfter(timestamp);
	}

	private static String parserCallTest(String payload) {
		String parserOutput = "";

		try {
			URL url = new URL("http://nycparse.herokuapp.com/api/parseaddresses");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			String input = "{\"source\":\"" + payload.replaceAll("\n", " ") + "\"}";

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			JSONArray addresses = null;
			JSONObject topObject = null;
			String output = "", JSON_DATA = null;

			while ((JSON_DATA = br.readLine()) != null) {
				output += JSON_DATA;
			}

			try {
				topObject = new JSONObject(output);
				addresses = topObject.getJSONArray("addresses");
				conn.disconnect();

				parserOutput = addresses.toString();
			} catch (JSONException e) {
				e.printStackTrace();
				parserOutput = "Parser Failed";
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			parserOutput = "Parser Failed";
		} catch (IOException e) {
			e.printStackTrace();
			parserOutput = "Parser Failed";
		}

		return "\"addresses\":\"" + pretty(parserOutput) + "\"";
	}

	private static String pretty(String parserOutput) {
		JsonParser parser = new JsonParser();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonElement el = parser.parse(parserOutput);
		parserOutput = gson.toJson(el);
		return parserOutput;
	}

	/**
	public static void main(String[] args) {
		String input = "NOTICE IS HEREBY GIVEN, pursuant to law, that the New York City Department of Consumer Affairs will hold a Public Hearing on Wednesday, January 28, 2015, at 2:00 P.M., at 66 John Street, 11th Floor, in the Borough of Manhattan, on the following petitions for sidewalk café revocable consent: 1. 132 Mulberry Inc. 132 Mulberry Street in the Borough of Manhattan.";
		String text = parserCallTest(input);
		System.out.println(text);
	}
	*/

}
