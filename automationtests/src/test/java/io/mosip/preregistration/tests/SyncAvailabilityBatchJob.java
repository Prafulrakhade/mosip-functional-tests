package io.mosip.preregistration.tests;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.internal.BaseTestMethod;

import io.mosip.preregistration.dao.PreregistrationDAO;
import io.mosip.service.ApplicationLibrary;
import io.mosip.service.BaseTestCase;
import io.mosip.util.PreRegistrationLibrary;
import io.restassured.response.Response;

public class SyncAvailabilityBatchJob extends BaseTestCase implements ITest {
	public Logger logger = Logger.getLogger(BatchJob.class);
	public PreRegistrationLibrary lib = new PreRegistrationLibrary();
	public String testSuite;
	public String preRegID = null;
	public String createdBy = null;
	public Response response = null;
	public String preID = null;
	protected static String testCaseName = "";
	public String folder = "preReg";
	public ApplicationLibrary applnLib = new ApplicationLibrary();
	public PreregistrationDAO dao=new PreregistrationDAO();
	
	
	
	@Test
	public void makeRegistartionCenterInactive() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre("10009");
		Response bookingResponse = lib.BookAppointment(documentResponse, avilibityResponse, preID);
		lib.compareValues(bookingResponse.jsonPath().get("response.bookingMessage").toString(),"Appointment booked successfully");
		dao.makeregistartionCenterDeActive("10009");
		Response syncAvailabilityResponse = lib.syncAvailability();
		lib.compareValues(syncAvailabilityResponse.jsonPath().get("response").toString(),"MASTER_DATA_SYNCED_SUCCESSFULLY");
		lib.FetchCentre("10009");
		dao.makeregistartionCenterActive("10009");
	}
	@Test
	public void makeRegCntrInactiveAndCheckAppointmentGettingCanceled() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre("10009");
		Response bookingResponse = lib.BookAppointment(documentResponse, avilibityResponse, preID);
		lib.compareValues(bookingResponse.jsonPath().get("response.bookingMessage").toString(),"Appointment booked successfully");
		dao.makeregistartionCenterDeActive("10009");
		Response syncAvailabilityResponse = lib.syncAvailability();
		lib.compareValues(syncAvailabilityResponse.jsonPath().get("response").toString(),"MASTER_DATA_SYNCED_SUCCESSFULLY");
		Response fetchAppointmentDetailsresponse = lib.FetchAppointmentDetails(preID);
		dao.makeregistartionCenterActive("10009");
	}
	@Test
	public void makeRegCntrInactiveAndCheckNotificationSendToEmail() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre("10009");
		Response bookingResponse = lib.BookAppointment(documentResponse, avilibityResponse, preID);
		lib.compareValues(bookingResponse.jsonPath().get("response.bookingMessage").toString(),"Appointment booked successfully");
		dao.makeregistartionCenterDeActive("10009");
		Response syncAvailabilityResponse = lib.syncAvailability();
		lib.compareValues(syncAvailabilityResponse.jsonPath().get("response").toString(),"MASTER_DATA_SYNCED_SUCCESSFULLY");
		dao.makeregistartionCenterActive("10009");
	}
	@Test
	public void makeRegCntrInactiveToActive() {
		testSuite = "Create_PreRegistration/createPreRegistration_smoke";
		JSONObject createPregRequest = lib.createRequest(testSuite);
		Response createResponse = lib.CreatePreReg(createPregRequest);
		String preID = createResponse.jsonPath().get("response.preRegistrationId").toString();
		Response documentResponse = lib.documentUpload(createResponse);
		Response avilibityResponse = lib.FetchCentre("10009");
		Response bookingResponse = lib.BookAppointment(documentResponse, avilibityResponse, preID);
		lib.compareValues(bookingResponse.jsonPath().get("response.bookingMessage").toString(),"Appointment booked successfully");
		dao.makeregistartionCenterDeActive("10009");
		Response syncAvailabilityResponse = lib.syncAvailability();
		lib.compareValues(syncAvailabilityResponse.jsonPath().get("response").toString(),"MASTER_DATA_SYNCED_SUCCESSFULLY");
		dao.makeregistartionCenterActive("10009");
		avilibityResponse = lib.FetchCentre("10009");
		lib.compareValues(avilibityResponse.jsonPath().get("response.regCenterId").toString(),"10009");
	}

	@Override
	public String getTestName() {
		return this.testCaseName;

	}
	@BeforeMethod(alwaysRun=true)
	public void login( Method method)
	{
		authToken=lib.getToken();
		testCaseName="preReg_BatchJob_" + method.getName();
	}


	@AfterMethod
	public void setResultTestName(ITestResult result, Method method) {
		try {
			BaseTestMethod bm = (BaseTestMethod) result.getMethod();
			Field f = bm.getClass().getSuperclass().getDeclaredField("m_methodName");
			f.setAccessible(true);
			f.set(bm, "preReg_BatchJob_" + method.getName());
		} catch (Exception ex) {
			Reporter.log("ex" + ex.getMessage());
		}
	}
}
