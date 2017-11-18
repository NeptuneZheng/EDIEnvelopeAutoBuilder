SET SCAN OFF

SET SERVEROUTPUT ON

-- TP_ID: @TP_ID
-- MSG_TYPE_ID: @MSG_TYPE_ID
-- DIR_ID: @DIR_ID
-- MSG_FMT_ID: @MSG_FMT_ID
-- MD5: @MD5
-- Operation: @OPERATION   //(incoming e2x, outgoing x2e)
-- IG Definition Script: @SCRIPT_NAME
-- Control Number - Sender: @CTRL_NUM_SENDER
-- Control Number - Receiver: @CTRL_NUM_RECEIVER
-- Control Number - Message Type: @CTRL_NUM_MSGTYPE
-- Control Number - Format: @CTRL_NUM_FORMAT
-- Transform settings in json format: @TRANSFORM_SETTINGS_JSON_STRING


SELECT tp_id, msg_type_id, dir_id, md5, update_ts, operation, ctrlnum_sender, ctrlnum_receiver, ctrlnum_msgtype, ctrlnum_format, transform_settings FROM B2B_EDI_BELUGA_CFG WHERE tp_id='@TP_ID' and msg_type_id='@MSG_TYPE_ID' and dir_id='@DIR_ID' and MSG_FMT_ID='@MSG_FMT_ID' ORDER BY update_ts DESC;

DECLARE 

CountCurrentItem SIMPLE_INTEGER:=0;
CurrentScriptVersion  varchar2 (100);

V_CTRLNUM_SENDER varchar2(35) := '@CTRL_NUM_SENDER';
V_CTRLNUM_RECEIVER varchar2(35) := '@CTRL_NUM_RECEIVER';
V_CTRLNUM_MSGTYPE varchar2(35) := '@CTRL_NUM_MSGTYPE';
V_CTRLNUM_FORMAT varchar2(35) := '@CTRL_NUM_FORMAT';
V_TRANSFORM_SETTINGS varchar2(1536) := '@TRANSFORM_SETTINGS_JSON_STRING';

CountScriptBody SIMPLE_INTEGER:=0;

@VAR64

BEGIN
    
    SELECT count(md5) INTO CountCurrentItem from B2B_EDI_BELUGA_CFG WHERE tp_id='@TP_ID' and msg_type_id='@MSG_TYPE_ID' and dir_id='@DIR_ID' and MSG_FMT_ID='@MSG_FMT_ID';
    
    IF CountCurrentItem > 0 THEN
    
        SELECT md5 INTO CurrentScriptVersion from B2B_EDI_BELUGA_CFG WHERE tp_id='@TP_ID' and msg_type_id='@MSG_TYPE_ID' and dir_id='@DIR_ID' and MSG_FMT_ID='@MSG_FMT_ID';
        
        IF (CurrentScriptVersion != '@MD5') THEN
          UPDATE B2B_EDI_BELUGA_CFG set md5='@MD5', update_ts=sysdate WHERE tp_id='@TP_ID' and msg_type_id='@MSG_TYPE_ID' and dir_id='@DIR_ID' and MSG_FMT_ID='@MSG_FMT_ID';
          dbms_output.put_line  ('1.1, Changed to new version (@MD5), previous version: ' || CurrentScriptVersion);
        ELSE
          dbms_output.put_line  ('1.1, MD5 version not change.');
        END IF;
        
        IF (TRIM(V_CTRLNUM_SENDER) is null) THEN
          dbms_output.put_line  ('1.2, CTRL_NUM_SENDER not change.');
        ELSE
          UPDATE B2B_EDI_BELUGA_CFG set CTRLNUM_SENDER='@CTRL_NUM_SENDER', update_ts=sysdate WHERE tp_id='@TP_ID' and msg_type_id='@MSG_TYPE_ID' and dir_id='@DIR_ID' and MSG_FMT_ID='@MSG_FMT_ID';
          dbms_output.put_line  ('1.2, CTRL_NUM_SENDER changed to: ' || V_CTRLNUM_SENDER);
        END IF;
        
        IF (TRIM(V_CTRLNUM_RECEIVER) is null) THEN
          dbms_output.put_line  ('1.3, CTRL_NUM_RECEIVER not change.');
        ELSE
          UPDATE B2B_EDI_BELUGA_CFG set CTRLNUM_RECEIVER='@CTRL_NUM_RECEIVER', update_ts=sysdate WHERE tp_id='@TP_ID' and msg_type_id='@MSG_TYPE_ID' and dir_id='@DIR_ID' and MSG_FMT_ID='@MSG_FMT_ID';
          dbms_output.put_line  ('1.3, CTRL_NUM_RECEIVER changed to: ' || V_CTRLNUM_RECEIVER);
        END IF;
        
        IF (TRIM(V_CTRLNUM_MSGTYPE) is null) THEN
          dbms_output.put_line  ('1.4, CTRL_NUM_MSGTYPE not change. ');
        ELSE
          UPDATE B2B_EDI_BELUGA_CFG set CTRLNUM_MSGTYPE='@CTRL_NUM_MSGTYPE', update_ts=sysdate WHERE tp_id='@TP_ID' and msg_type_id='@MSG_TYPE_ID' and dir_id='@DIR_ID' and MSG_FMT_ID='@MSG_FMT_ID';
          dbms_output.put_line  ('1.4, CTRL_NUM_MSGTYPE changed to: ' || V_CTRLNUM_MSGTYPE);
        END IF;
        
        IF (TRIM(V_CTRLNUM_FORMAT) is null) THEN
          dbms_output.put_line  ('1.5, CTRL_NUM_FORMAT not change.');
        ELSE
          UPDATE B2B_EDI_BELUGA_CFG set CTRLNUM_FORMAT='@CTRL_NUM_FORMAT', update_ts=sysdate WHERE tp_id='@TP_ID' and msg_type_id='@MSG_TYPE_ID' and dir_id='@DIR_ID' and MSG_FMT_ID='@MSG_FMT_ID';
          dbms_output.put_line  ('1.5, CTRL_NUM_FORMAT changed to: ' || V_CTRLNUM_FORMAT);
        END IF;
        
        IF (TRIM(V_TRANSFORM_SETTINGS) is null) THEN
          dbms_output.put_line  ('1.6, TRANSFORM_SETTINGS not change.');
        ELSE
          UPDATE B2B_EDI_BELUGA_CFG set TRANSFORM_SETTINGS = V_TRANSFORM_SETTINGS, update_ts=sysdate WHERE tp_id='@TP_ID' and msg_type_id='@MSG_TYPE_ID' and dir_id='@DIR_ID' and MSG_FMT_ID='@MSG_FMT_ID';
          dbms_output.put_line  ('1.6, TRANSFORM_SETTINGS changed new value. ');
        END IF;
        
    ELSE 
    
        INSERT INTO B2B_EDI_BELUGA_CFG (tp_id, msg_type_id, dir_id, MSG_FMT_ID, md5, update_ts, operation, ctrlnum_sender, ctrlnum_receiver, ctrlnum_msgtype, ctrlnum_format, transform_settings) 
             values ('@TP_ID', '@MSG_TYPE_ID', '@DIR_ID', '@MSG_FMT_ID', '@MD5', sysdate, '@OPERATION', '@CTRL_NUM_SENDER', '@CTRL_NUM_RECEIVER', '@CTRL_NUM_MSGTYPE', '@CTRL_NUM_FORMAT', V_TRANSFORM_SETTINGS);
             
        dbms_output.put_line  ('1, Add data B2B_EDI_BELUGA_CFG (@TP_ID, @MSG_TYPE_ID, @DIR_ID, @MSG_FMT_ID), MD5: @MD5. ');
        
    END IF;

    
    
    SELECT count(md5) INTO CountScriptBody from B2B_EDI_BELUGA_SCRIPT_BODY WHERE md5='@MD5';
    
    IF CountScriptBody = 0 THEN
        INSERT INTO B2B_EDI_BELUGA_SCRIPT_BODY (MD5, SCRIPT_NAME, SCRIPT, UPDATE_TS) values ('@MD5', '@SCRIPT_NAME', @VARIABLE_64, sysdate);
        dbms_output.put_line  ('2, Add data B2B_EDI_BELUGA_SCRIPT_BODY: @MD5');
    ELSE 
        dbms_output.put_line  ('2, MD5 (@MD5) exists already, no action.');
    END IF;

END;
/

COMMIT;

SELECT tp_id, msg_type_id, dir_id, MSG_FMT_ID, md5, update_ts, operation, ctrlnum_sender, ctrlnum_receiver, ctrlnum_msgtype, ctrlnum_format, transform_settings FROM B2B_EDI_BELUGA_CFG WHERE tp_id='@TP_ID' and msg_type_id='@MSG_TYPE_ID' and dir_id='@DIR_ID' ORDER BY update_ts DESC;

