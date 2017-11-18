SET SCAN OFF
SET SERVEROUTPUT ON

-- For PMT Groovy Script : B2B_EDI_ALPACA_CFG (tp_id, msg_type_id, dir_id, msg_fmt_id, proc_type_id) and B2B_EDI_ALPACA_SCRIPT (ref_key, stage, md5), B2B_EDI_ALPACA_SCRIPT_BODY
-- Reference: @REFNAME
-- TpId: @TP_ID
-- Msg Type Id: @MSG_TYPE_ID
-- Dir ID: @DIR_ID
-- Process Type ID: @PROC_TYPE_ID , possible values: TRANSLATE / PREPROCESS / POSTPROCESS
-- Reference Key: @PMT_REF_KEY : MD5 of TP_ID + MSG_TYPE_ID + DIR_ID + MSG_FMT_ID + PROC_TYPE_ID
-- MD5: @MD5
-- Groovy Script Class Name: @SCRIPT_CLASS_NAME
-- Stage: StandardMappingStage always
-- type of chain: KUKRI always


select tp_id, msg_type_id, dir_id, MSG_FMT_ID, proc_type_id, type_of_chain, update_ts from B2B_EDI_ALPACA_CFG where ref_key = '@PMT_REF_KEY';

select ref_key, stage, md5, update_ts from B2B_EDI_ALPACA_SCRIPT where ref_key = '@PMT_REF_KEY';

select MD5, SCRIPT_NAME from B2B_EDI_ALPACA_SCRIPT_BODY where MD5 = '@MD5';


DECLARE 

CountAlpacaCfg SIMPLE_INTEGER:=0;

CountAlpacaScript SIMPLE_INTEGER:=0;

AlpacaScriptCurrentVersion  varchar2 (100);

CountGroovyScriptBody SIMPLE_INTEGER:=0;

@VAR64

BEGIN
    
    SELECT count(ref_key) INTO CountAlpacaCfg from B2B_EDI_ALPACA_CFG WHERE tp_id='@TP_ID' and msg_type_id='@MSG_TYPE_ID' and dir_id='@DIR_ID' and MSG_FMT_ID='@MSG_FMT_ID' and proc_type_id='@PROC_TYPE_ID';
    IF CountAlpacaCfg = 0 THEN
        INSERT INTO B2B_EDI_ALPACA_CFG (TP_ID, MSG_TYPE_ID, DIR_ID, MSG_FMT_ID, TYPE_OF_CHAIN, UPDATE_TS, REF_KEY, PROC_TYPE_ID) values ('@TP_ID', '@MSG_TYPE_ID', '@DIR_ID', '@MSG_FMT_ID', 'KUKRI', sysdate, '@PMT_REF_KEY', '@PROC_TYPE_ID');
        dbms_output.put_line  ('1, add b2b_edi_alpaca_cfg with: @TP_ID, @MSG_TYPE_ID, @DIR_ID, @MSG_FMT_ID.');
    ELSE 
        dbms_output.put_line  ('1, No action to b2b_edi_alpaca_cfg as PMT exists already. ');
    END IF;
    
    
    SELECT count(ref_key) INTO CountAlpacaScript from B2B_EDI_ALPACA_SCRIPT WHERE ref_key = '@PMT_REF_KEY';
    IF CountAlpacaScript = 0 THEN
        INSERT INTO B2B_EDI_ALPACA_SCRIPT (REF_KEY, STAGE, MD5, UPDATE_TS) values ('@PMT_REF_KEY', 'StandardMappingStage', '@MD5', sysdate);
        dbms_output.put_line  ('2, Add alpaca script with ref_key: @PMT_REF_KEY. ');
    ELSE 
        SELECT MD5 INTO AlpacaScriptCurrentVersion from B2B_EDI_ALPACA_SCRIPT WHERE ref_key = '@PMT_REF_KEY';
        IF AlpacaScriptCurrentVersion = '@MD5' THEN
            dbms_output.put_line  ('2, Alpaca script, no action as same as before (@MD5).');
        ELSE 
            UPDATE B2B_EDI_ALPACA_SCRIPT SET MD5 = '@MD5', update_ts = sysdate WHERE ref_key = '@PMT_REF_KEY';
            dbms_output.put_line  ('2, changed Alpaca script for @PMT_REF_KEY, to new version (@MD5), previous version is (' || AlpacaScriptCurrentVersion || '). ');
        END IF;
    END IF;
    
    
    SELECT count(MD5) INTO CountGroovyScriptBody from B2B_EDI_ALPACA_SCRIPT_BODY WHERE MD5 = '@MD5';
    IF CountGroovyScriptBody = 0 THEN
        INSERT INTO B2B_EDI_ALPACA_SCRIPT_BODY (MD5, SCRIPT_NAME, SCRIPT, UPDATE_TS) values ('@MD5', '@SCRIPT_CLASS_NAME', @VARIABLE_64, sysdate);
        dbms_output.put_line  ('3, Add script body with MD5: @MD5 and script name: @SCRIPT_CLASS_NAME. ');
    ELSE 
        dbms_output.put_line  ('3, Alpaca script body exists for version @MD5 already, no action now.');
    END IF;
    
END;
/

COMMIT;


select tp_id, msg_type_id, dir_id, MSG_FMT_ID, proc_type_id, type_of_chain, update_ts from B2B_EDI_ALPACA_CFG where ref_key = '@PMT_REF_KEY';

select ref_key, stage, md5, update_ts from B2B_EDI_ALPACA_SCRIPT where ref_key = '@PMT_REF_KEY';

select MD5, SCRIPT_NAME from B2B_EDI_ALPACA_SCRIPT_BODY where MD5 = '@MD5';

