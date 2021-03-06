/*
 * ============LICENSE_START=======================================================
 * ves-agent
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import evel_javalibrary.att.com.*;
import evel_javalibrary.att.com.AgentMain.EVEL_ERR_CODES;
import evel_javalibrary.att.com.EvelFault.EVEL_SEVERITIES;
import evel_javalibrary.att.com.EvelFault.EVEL_SOURCE_TYPES;
import evel_javalibrary.att.com.EvelFault.EVEL_VF_STATUSES;
import evel_javalibrary.att.com.EvelHeader.PRIORITIES;
import evel_javalibrary.att.com.EvelMobileFlow.MOBILE_GTP_PER_FLOW_METRICS;
import evel_javalibrary.att.com.EvelScalingMeasurement.MEASUREMENT_CPU_USE;
import evel_javalibrary.att.com.EvelScalingMeasurement.MEASUREMENT_MEM_USE;
import evel_javalibrary.att.com.EvelScalingMeasurement.MEASUREMENT_LATENCY_BUCKET;
import evel_javalibrary.att.com.EvelScalingMeasurement.MEASUREMENT_DISK_USE;
import evel_javalibrary.att.com.EvelScalingMeasurement.MEASUREMENT_NIC_PERFORMANCE;
import evel_javalibrary.att.com.EvelStateChange.EVEL_ENTITY_STATE;
import evel_javalibrary.att.com.EvelThresholdCross.EVEL_ALERT_TYPE;
import evel_javalibrary.att.com.EvelThresholdCross.EVEL_EVENT_ACTION;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.BasicConfigurator;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;


@PrepareForTest({AgentMain.class})
@RunWith(PowerMockRunner.class)
public class TestJunit {

   private AgentMain  mymainmock = null;
   private EvelHeader header = null;

   //private static final Logger LOG = LoggerFactory.getLogger(TestJunit.class);
   private static final Logger LOG = Logger.getLogger(TestJunit.class.getName());

    @Before
    public void setupClass() {
        BasicConfigurator.configure();
        mymainmock = mock(AgentMain.class);

        // PowerMockito does bytecode magic to mock static methods and use final classes
        PowerMockito.mockStatic(AgentMain.class);

        //evel init
         try {
          mymainmock.evel_initialize( "http://127.0.0.1", 30000, "/vendor_event_listener", "/example_vnf", null, null,null, "pill", "will", Level.DEBUG);
         } catch ( Exception e )
         {
           e.printStackTrace();
         }
    }


   @Test
   public void testHeartbeat() {

              header  = EvelHeader.evel_new_heartbeat("Hearbeat_vAFX","vmname_ip");
              header.evel_nfnamingcode_set("vVNF");
              header.evel_nfcnamingcode_set("vVNF");
              when(mymainmock.evel_post_event(header)).thenReturn(true);
              boolean ret = mymainmock.evel_post_event(header);
              LOG.info("Returned "+ret);
              assertTrue( ret );
   }


   @Test
   public void testFault() {

              EvelFault flt  = new EvelFault("Fault_vVNF", "vmname_ip",
            		  "NIC error", "Hardware failed",
                  EvelHeader.PRIORITIES.EVEL_PRIORITY_HIGH,
                  EVEL_SEVERITIES.EVEL_SEVERITY_MAJOR,
                  EVEL_SOURCE_TYPES.EVEL_SOURCE_CARD,
                  EVEL_VF_STATUSES.EVEL_VF_STATUS_ACTIVE);
              flt.evel_fault_addl_info_add("nichw", "fail");
              flt.evel_fault_addl_info_add("nicsw", "fail");
              flt.evel_fault_category_set("intftype");
              flt.evel_fault_interface_set("eth0");
              flt.evel_fault_type_set("vmintf");
              when(mymainmock.evel_post_event(flt)).thenReturn(true);
              boolean ret = mymainmock.evel_post_event(flt);
              LOG.info("Returned "+ret);
              assertTrue( ret );

   }

   @Test
   public void testBatch() {
              EvelBatch be = new EvelBatch();
              EvelFault flt2  = new EvelFault("Fault_vVNF", "vmname_ip",
            		  "NIC error", "Hardware failed",
                  EvelHeader.PRIORITIES.EVEL_PRIORITY_HIGH,
                  EVEL_SEVERITIES.EVEL_SEVERITY_MAJOR,
                  EVEL_SOURCE_TYPES.EVEL_SOURCE_CARD,
                  EVEL_VF_STATUSES.EVEL_VF_STATUS_ACTIVE);
              flt2.evel_fault_addl_info_add("nichw", "fail");
              flt2.evel_fault_addl_info_add("nicsw", "fail");
              be.addEvent(flt2);
              
              EvelFault flt3  = new EvelFault("Fault_vVNF", "vmname_ip2",
            		  "NIC error", "Hardware failed",
                  EvelHeader.PRIORITIES.EVEL_PRIORITY_NORMAL,
                  EVEL_SEVERITIES.EVEL_SEVERITY_MAJOR,
                  EVEL_SOURCE_TYPES.EVEL_SOURCE_CARD,
                  EVEL_VF_STATUSES.EVEL_VF_STATUS_ACTIVE);
              flt3.evel_fault_type_set("Interface fault");
              flt3.evel_fault_category_set("Failed category");
              flt3.evel_fault_interface_set("An Interface Card");
              flt3.evel_fault_addl_info_add("nichw", "fail");
              flt3.evel_fault_addl_info_add("nicsw", "fail");
              be.addEvent(flt3);
 

              EvelStateChange stc  = new EvelStateChange("StateChange_vVNF", "vmname_ip",
            		      EVEL_ENTITY_STATE.EVEL_ENTITY_STATE_IN_SERVICE,
                          EVEL_ENTITY_STATE.EVEL_ENTITY_STATE_OUT_OF_SERVICE,"bgp");
              stc.evel_statechange_addl_info_add("bgpa", "fail");
              stc.evel_statechange_addl_info_add("bgpb", "fail");
              //AgentMain.evel_post_event(stc);

              be.addEvent(stc);
              when(mymainmock.evel_post_event(be)).thenReturn(true);
              boolean ret = mymainmock.evel_post_event(be);
              LOG.info("Returned "+ret);
              assertTrue( ret );
   }

   @Test
   public void testMeasurement() {
              EvelScalingMeasurement sm  = new EvelScalingMeasurement(10.0,"Measurements_vVNF", "vmname_ip");
              sm.evel_measurement_type_set("dummy");
              sm.evel_measurement_addl_info_add("meas1","value1");
              sm.evel_measurement_addl_info_add("meas2","value2");
              sm.evel_measurement_concurrent_sessions_set(3);
              sm.evel_measurement_config_entities_set(3);
              sm.evel_measurement_myerrors_set(10,20,30,40);
              sm.evel_measurement_mean_req_lat_set(123.0);
              sm.evel_measurement_request_rate_set(12);
              MEASUREMENT_CPU_USE my1 = sm.evel_measurement_new_cpu_use_add("cpu1", 100.0);
              my1.idle.SetValue(20.0);
              my1.sys.SetValue(21.0);
              MEASUREMENT_CPU_USE my2 = sm.evel_measurement_new_cpu_use_add("cpu2", 10.0);
              my2.steal.SetValue(34.0);
              my2.user.SetValue(32.0);
              sm.evel_measurement_custom_measurement_add("group1","name1","val1");
              sm.evel_measurement_custom_measurement_add("group1","name2","val2");
              sm.evel_measurement_custom_measurement_add("group2","name1","val1");
              sm.evel_measurement_custom_measurement_add("group2","name2","val2");

           JsonObjectBuilder jsonObjBld1 =
                      Json.createObjectBuilder()
                              .add("trackIdentifier", 12345)
                              .add("remoteSource", "vm1235")
                              .add("ended", "12:45:67")
                              .add("detached", 633453)
                              .add("frameWidth", 765765)
                              .add("frameHeight", 767867)
                              .add("framesPerSecond", 334343)
                              .add("framesSent", 8976786)
                              .add("framesReceived", 233423)
                              .add("frameHeight", 8897896)
                              .add("framesDecoded", 3434533)
                              .add("framesDropped", 87867676)
                              .add("framesCorrupted", 3345342)
                              .add("audioLevel", "-45dBm");
              JsonObject custom1 = jsonObjBld1.build();
              sm.evel_measurement_add_jsonobj(custom1);
            JsonObjectBuilder jsonObjBld2 =
                      Json.createObjectBuilder()
                              .add("trackIdentifier", 12345)
                              .add("remoteSource", "vm1235")
                              .add("ended", "12:45:67")
                              .add("detached", 633453)
                              .add("frameWidth", 765765)
                              .add("frameHeight", 767867)
                              .add("framesPerSecond", 334343)
                              .add("framesSent", 8976786)
                              .add("framesReceived", 233423)
                              .add("frameHeight", 8897896)
                              .add("framesDecoded", 3434533)
                              .add("framesDropped", 87867676)
                              .add("framesCorrupted", 3345342)
                              .add("audioLevel", "-45dBm");
              JsonObject custom2 = jsonObjBld2.build();
              sm.evel_measurement_add_jsonobj(custom2);

              sm.evel_measurement_cpu_use_idle_set(my1,0.5);
              sm.evel_measurement_cpu_use_interrupt_set(my1,0.5);
              sm.evel_measurement_cpu_use_nice_set(my1,0.5);
              sm.evel_measurement_cpu_use_softirq_set(my1,0.5);
              sm.evel_measurement_cpu_use_steal_set(my1,0.5);
              sm.evel_measurement_cpu_use_system_set(my1,0.5);
              sm.evel_measurement_cpu_use_usageuser_set(my1,0.5);

              MEASUREMENT_MEM_USE mym1 = sm.evel_measurement_new_mem_use_add("mem1",123456.0);
              sm.evel_measurement_mem_use_memcache_set(mym1, 456.);
	      sm.evel_measurement_mem_use_memconfig_set(mym1, 456.);
	      sm.evel_measurement_mem_use_memfree_set(mym1, 456.);
	      sm.evel_measurement_mem_use_slab_reclaimed_set(mym1, 456.);
	      sm.evel_measurement_mem_use_slab_unreclaimable_set(mym1, 456.);
	      sm.evel_measurement_mem_use_usedup_set(mym1, 456.);

              MEASUREMENT_DISK_USE dsk = sm.evel_measurement_new_disk_use_add("disk1");
	      sm.evel_measurement_disk_use_iotimeavg_set(dsk, 1.0); 
	      sm.evel_measurement_disk_use_iotimelast_set(dsk, 1.0); 
	      sm.evel_measurement_disk_use_iotimemax_set(dsk, 100.0); 
	      sm.evel_measurement_disk_use_mergereadavg_set(dsk, 67.);
	      sm.evel_measurement_disk_use_mergereadlast_set(dsk, 67.);
	      sm.evel_measurement_disk_use_mergereadmax_set(dsk, 678.);
	      sm.evel_measurement_disk_use_mergereadmin_set(dsk, 678.);
	      sm.evel_measurement_disk_use_mergewritelast_set(dsk,456.);
	      sm.evel_measurement_disk_use_mergewritemax_set(dsk,678.);
	      sm.evel_measurement_disk_use_mergewritemin_set(dsk,67.);
	      sm.evel_measurement_disk_use_octetsreadavg_set(dsk,65.);
	      sm.evel_measurement_disk_use_octetsreadlast_set(dsk,78.);
	      sm.evel_measurement_disk_use_octetsreadmax_set(dsk,56.);
	      sm.evel_measurement_disk_use_octetsreadmin_set(dsk,345.);
	      sm.evel_measurement_disk_use_octetswriteavg_set(dsk,34.);
	      sm.evel_measurement_disk_use_octetswritelast_set(dsk,676.);
	      sm.evel_measurement_disk_use_octetswritemax_set(dsk, 56.);
	      sm.evel_measurement_disk_use_octetswritemin_set(dsk,678.);
	      sm.evel_measurement_disk_use_opsreadavg_set(dsk,67.);
	      sm.evel_measurement_disk_use_opsreadlast_set(dsk,33.);
	      sm.evel_measurement_disk_use_opsreadmax_set(dsk,678.);
	      sm.evel_measurement_disk_use_opsreadmin_set(dsk,23.);
	      sm.evel_measurement_disk_use_opswriteavg_set(dsk,23.);
	      sm.evel_measurement_disk_use_opswritelast_set(dsk,23.);
	      sm.evel_measurement_disk_use_opswritemax_set(dsk,23.);
	      sm.evel_measurement_disk_use_opswritemin_set(dsk,23.);

	      sm.evel_measurement_disk_use_pendingopsavg_set(dsk,23.);
	      sm.evel_measurement_disk_use_pendingopslast_set(dsk,23.);
	      sm.evel_measurement_disk_use_pendingopsmax_set(dsk,23.);
	      sm.evel_measurement_disk_use_pendingopsmin_set(dsk,23.);

	      sm.evel_measurement_disk_use_timereadavg_set(dsk,45.);
	      sm.evel_measurement_disk_use_timereadlast_set(dsk,45.);
	      sm.evel_measurement_disk_use_timereadmax_set(dsk,45.);
	      sm.evel_measurement_disk_use_timereadmin_set(dsk,45.);

	      sm.evel_measurement_disk_use_timewriteavg_set(dsk,45.);
	      sm.evel_measurement_disk_use_timewritelast_set(dsk,45.);
	      sm.evel_measurement_disk_use_timewritemax_set(dsk,45.);
	      sm.evel_measurement_disk_use_timewritemin_set(dsk,45.);


              MEASUREMENT_NIC_PERFORMANCE vnic_p = sm.evel_measurement_new_vnic_performance("vnic1","true");
              vnic_p.recvd_bcast_packets_acc.SetValue(2400000.0);
              vnic_p.recvd_mcast_packets_delta.SetValue(5677888.0);
              vnic_p.recvd_mcast_packets_acc.SetValue(5677888.0);
              vnic_p.tx_ucast_packets_acc.SetValue(547856576.0);
              vnic_p.tx_ucast_packets_delta.SetValue(540000.0);
              sm.evel_meas_vnic_performance_add(vnic_p);
              sm.evel_measurement_fsys_use_add("fs1",40000.0, 5678.0, 5432.0,45.0,67.0,78.0);
              sm.evel_measurement_feature_use_add("latefeature",40);
              sm.evel_measurement_codec_use_add("codec",41);

              MEASUREMENT_LATENCY_BUCKET myb = sm.evel_new_meas_latency_bucket(10);
              sm.evel_meas_latency_bucket_high_end_set(myb,1000.);
              sm.evel_meas_latency_bucket_low_end_set(myb,1.);
              sm.evel_meas_latency_bucket_add(myb);
              sm.evel_measurement_latency_add(1.,10.,20);
              
              /*
               * Arguments updated 15/07/2018
               */

              sm.evel_measurement_vnic_performance_add("vnic","vals","5 mbps", 1.,2.,3.,4.,5.,6.,7.,8.,9.,10.,11.,12.,13.,14.,15.,16.,17.,18.,19.,20.,21.,22.,23.,24.,25.,26.,27.,28.,29.,30.,31.,32.,33.,34.,35.,36.);

              when(mymainmock.evel_post_event(sm)).thenReturn(true);
              boolean ret = mymainmock.evel_post_event(sm);
              LOG.info("Returned "+ret);
              assertTrue( ret );
   }
              
   @Test
   public void testSyslog() {
              EvelSyslog sysl = new EvelSyslog("Syslog_vVNF", "vmname_ip",
            		                    EvelFault.EVEL_SOURCE_TYPES.EVEL_SOURCE_ROUTER,
            		                   "Router failed","JUNIPER");
              sysl.evel_syslog_proc_id_set(456);
              sysl.evel_syslog_proc_set("routed");
              sysl.evel_syslog_type_set("named");
              sysl.evel_syslog_addl_filter_set("syslfilter");
              sysl.evel_syslog_event_source_host_set("vm2474");
              sysl.evel_syslog_facility_set(EvelSyslog.EVEL_SYSLOG_FACILITIES.EVEL_SYSLOG_FACILITY_LOCAL7);
              sysl.evel_syslog_severity_set("Warning");
              sysl.evel_syslog_version_set(10);
              sysl.evel_syslog_s_data_set("hello");
              sysl.evel_syslog_sdid_set("hello");
              sysl.evel_syslog_severity_set("critical");
              
              when(mymainmock.evel_post_event(sysl)).thenReturn(true);
              boolean ret = mymainmock.evel_post_event(sysl);
              LOG.info("Returned "+ret);
              assertTrue( ret );
   }
              
   @Test
   public void testHtbtField() {
              EvelHeartbeatField hfld = new EvelHeartbeatField(123,"HeartbeatField_vVNF", "vmname_ip");
              hfld.evel_hrtbt_interval_set(23);
              hfld.evel_hrtbt_field_addl_info_add("bgpb", "fail");
              hfld.evel_hrtbt_interval_set(100);
              
              when(mymainmock.evel_post_event(hfld)).thenReturn(true);
              boolean ret = mymainmock.evel_post_event(hfld);
              LOG.info("Returned "+ret);
              assertTrue( ret );
   }


   @Test
   public void testEvelMobileFlow() {
              EvelMobileFlow mf1 = new EvelMobileFlow("hello","there");
              EvelMobileFlow.MOBILE_GTP_PER_FLOW_METRICS gtp = mf1.new MOBILE_GTP_PER_FLOW_METRICS(12.0, 13.0, 1,2,3,4,5,6,7,new Date(), "mflow", 8,9,10,11,12,13,14,15,16, 17,18,19,20,21,22,23,24,25,26);
              mf1.gtp_per_flow_metrics = gtp;

              EvelMobileFlow mf = new EvelMobileFlow("MobileFlow_12", "MF_12345", "flowdir", gtp, "iptyp", 
		"ipv4", "ipendp", 100, "endpou",1234);
              mf.evel_mobile_flow_addl_field_add("bgpb", "fail");
              mf.evel_mobile_flow_type_set("named");
              mf.evel_mobile_flow_app_type_set("named");

              when(mymainmock.evel_post_event(mf)).thenReturn(true);
              boolean ret = mymainmock.evel_post_event(mf);
              LOG.info("Returned "+ret);
              assertTrue( ret );
   }


   @Test
   public void testSipSignaling() {
              
              EvelSipSignaling sip = new EvelSipSignaling("SipSignaling_vVNF", "vmname_ip","aricent","corlator","127.0.0.1","5647","10.1.1.124","5678");
              sip.evel_signaling_type_set("BVOIP");
              sip.evel_signaling_local_ip_address_set("1.2.3.4");
              sip.evel_signaling_local_port_set("446");
              sip.evel_signaling_remote_ip_address_set("1.2.3.4");
              sip.evel_signaling_remote_port_set("1446");
              sip.evel_signaling_vnfmodule_name_set("sipmc");
              sip.evel_signaling_vnfname_set("SIPVM");
              sip.evel_signaling_compressed_sip_set("comprsip");
              sip.evel_signaling_summary_sip_set("summsip");
              sip.evel_signaling_correlator_set("corl");
              sip.evel_signaling_addl_info_add("bgpb", "fail");

              when(mymainmock.evel_post_event(sip)).thenReturn(true);
              boolean ret = mymainmock.evel_post_event(sip);
              LOG.info("Returned "+ret);
              assertTrue( ret );
   }
              
   @Test
   public void testVoiceQuality() {
              EvelVoiceQuality vq = new EvelVoiceQuality("VoiceQuality_vVNF", "vmname_ip",
            		  "calleeSideCodc",
      			    "callerSideCodc", "corlator",
    			    "midCllRtcp", "juniper");
              /*
               * Arguments updated 15/07/2018
               */
              vq.evel_voice_quality_end_metrics_set("adjname", "Caller", 20, 30, 40, 50, 60, 70, 80, 100, 110, 120, 130, 140, 15.1, 160.12, 170, 190, 200,210,220,230,240,250,260,270,280,290,300);
              //vq.evel_voice_quality_end_metrics_set("adjname", "Caller", 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 15.1, 160.12, 170, 190, 200,210,220,230,240,250,260,270,280,290,300);
              vq.evel_voice_quality_addl_info_add("bgpb", "fail");
              vq.evel_voice_quality_callee_codec_set("codec1");
              vq.evel_voice_quality_caller_codec_set("codec1");
              vq.evel_voice_quality_correlator_set("codec1");
              vq.evel_voice_quality_rtcp_data_set("codec1");
              vq.evel_voice_quality_vnfmodule_name_set("mod1");
              vq.evel_voice_quality_vnfname_set("mod1");
              vq.evel_voice_quality_phone_number_set("1234-567-8910");
 /*
  * Arguments updated 15/07/2018
  */
              
          //    vq.evel_voice_quality_end_metrics_set("name","descr",2.,3.,4.,5.,6.,7.,8.,9.,10.,11.,12,13.,14.,15.,16.,17.,19.,20.,21.,22.,23.,24.,25.,26.,27.,28.,29.,30.);
              
              when(mymainmock.evel_post_event(vq)).thenReturn(true);
              boolean ret = mymainmock.evel_post_event(vq);
              LOG.info("Returned "+ret);
              assertTrue( ret );
   }
              
   @Test
   public void testEvelOther() {
              EvelOther ev = new EvelOther("MyCustomEvent_vVNF", "vmname_ip");
              ev.evel_other_field_add("a1", "b1");
              ev.evel_other_field_add("a1", "b2");
              
              ev.evel_other_field_add_namedarray("a1", "b1", "c1");
              ev.evel_other_field_add_namedarray("a1", "b2", "c2");
              ev.evel_other_field_add_namedarray("a2", "b1", "c1");
              ev.evel_other_field_add_namedarray("a2", "b1", "c1");

              JsonObjectBuilder jsonObjBld =
                      Json.createObjectBuilder()
                              .add("trackIdentifier", 12345)
                              .add("remoteSource", "vm1235")
                              .add("ended", "12:45:67")
                              .add("detached", 633453)
                              .add("frameWidth", 765765)
                              .add("frameHeight", 767867)
                              .add("framesPerSecond", 334343)
                              .add("framesSent", 8976786)
                              .add("framesReceived", 233423)
                              .add("frameHeight", 8897896)
                              .add("framesDecoded", 3434533)
                              .add("framesDropped", 87867676)
                              .add("framesCorrupted", 3345342)
                              .add("audioLevel", "-45dBm");
              JsonObject custom = jsonObjBld.build();

              ev.evel_other_field_add_jsonobj(custom);
              
              when(mymainmock.evel_post_event(ev)).thenReturn(true);
              boolean ret = mymainmock.evel_post_event(ev);
              LOG.info("Returned "+ret);
              assertTrue( ret );
   }
              
   @Test
   public void testThresholdCross() {
      		String dateStart = "01/14/2012 09:29:58";
      		String dateStop = "01/15/2012 10:31:48";

      		//HH converts hour in 24 hours format (0-23), day calculation
      		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

      		Date d1 = null;
      		Date d2 = null;

      		try {
      			d1 = format.parse(dateStart);
      			d2 = format.parse(dateStop);
      		}catch (Exception e) {
    			e.printStackTrace();
    		}
              
              
//              EvelThresholdCross tca = new EvelThresholdCross("ThresholdCross_vVNF", "vmname_ip",
//              "CRIT",
//              "mcast Limit reached",
//              "mcastRxPackets",
//              "1250000000",
//              EvelThresholdCross.EVEL_EVENT_ACTION.EVEL_EVENT_ACTION_SET,
//              "Mcast Rx breached", 
//              EvelThresholdCross.EVEL_ALERT_TYPE.EVEL_ELEMENT_ANOMALY,
//              d1, 
//              EvelThresholdCross.EVEL_SEVERITIES.EVEL_SEVERITY_CRITICAL,
//              d2);
              
              EvelThresholdCross tca = new EvelThresholdCross("ThresholdCross_vVNF", "vmname_ip", "CRIT", 
              		"mcastRxPackets", EvelThresholdCross.EVEL_EVENT_ACTION.EVEL_EVENT_ACTION_CLEAR, 
              		"Mcast Rx breached", 
              		EvelThresholdCross.EVEL_ALERT_TYPE.EVEL_CARD_ANOMALY, 
              		
              		d1, EvelThresholdCross.EVEL_SEVERITIES.EVEL_SEVERITY_CRITICAL, 
              		d2);
              tca.evel_threshold_cross_interfacename_set("ns345");
              tca.evel_thresholdcross_addl_info_add("n1", "v1");
              tca.evel_thresholdcross_addl_info_add("n2", "v2");
              tca.evel_thresholdcross_alertid_add("alert1");
              tca.evel_thresholdcross_alertid_add("alert2");
              tca.evel_threshold_cross_possible_rootcause_set("ns345");
              tca.evel_threshold_cross_networkservice_set("ns345");
              tca.evel_threshold_cross_interfacename_set("eth0");
              tca.evel_threshold_cross_data_elementtype_set("alert2");
              tca.evel_threshold_cross_data_collector_set("alert2");
              tca.evel_threshold_cross_alertvalue_set("value");
              
              when(mymainmock.evel_post_event(tca)).thenReturn(true);
              boolean ret = mymainmock.evel_post_event(tca);
              LOG.info("Returned "+ret);
              assertTrue( ret );
   }
              

   @Test
   public void testMobileFlow() {
      		String dateStart = "01/14/2012 09:29:58";
      		Date d1 = null;
      		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      		try {
      			d1 = format.parse(dateStart);
      		}catch (Exception e) {
    			e.printStackTrace();
    		}
              EvelMobileFlow mf = new EvelMobileFlow("MobileFlow_vVNF", "vmname_ip",
            		  "In",
                      null,
                      "GTP",
                       "v2.3",
                      "1.2.3.4",
                      345556,
                      "5.6.7.8",
                      334344);
              MOBILE_GTP_PER_FLOW_METRICS mygtp = mf.new MOBILE_GTP_PER_FLOW_METRICS(
                      1.01,
                      2.02,
                      3,
                      4,
                      5,
                      6,
                      7,
                      8,
                      9,
                      d1,
                      "ACTIVE",
                      10,
                      11,
                      12,
                      13,
                      14,
                      15,
                      16,
                      17,
                      18,
                      19,
                      20,
                      21,
                      22,
                      23,
                      24,
                      25,
                      26,
                      27,
                      28);
              mf.gtp_per_flow_metrics = mygtp;
              
              when(mymainmock.evel_post_event(mf)).thenReturn(true);
              boolean ret = mymainmock.evel_post_event(mf);
              LOG.info("Returned "+ret);
              assertTrue( ret );
   }


}

