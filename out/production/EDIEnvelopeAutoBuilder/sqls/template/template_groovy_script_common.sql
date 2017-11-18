SET SCAN OFF
SET SERVEROUTPUT ON

-- For Common Groovy Script : B2B_EDI_ALPACA_SCRIPT_COMMON, B2B_EDI_ALPACA_SCRIPT_BODY
-- Reference: @REFNAME
-- Groovy Script Class Name: @SCRIPT_CLASS_NAME
-- Script Type: @SCRIPT_TYPE
-- MD5: @MD5


SELECT SCRIPT_TYPE, SCRIPT_CLASS_NAME, MD5, UPDATE_TS, REMARKS FROM b2b_edi_alpaca_script_common WHERE SCRIPT_CLASS_NAME = '@SCRIPT_CLASS_NAME';


DECLARE 

CountScriptCommon SIMPLE_INTEGER:=0;

CountScriptBody SIMPLE_INTEGER:=0;

CurrentScriptBodyVersion varchar2(100);

@VAR64

BEGIN
    
    SELECT count(md5) INTO CountScriptCommon from b2b_edi_alpaca_script_common WHERE SCRIPT_CLASS_NAME = '@SCRIPT_CLASS_NAME';
    
    IF CountScriptCommon = 0 THEN
        
        INSERT INTO b2b_edi_alpaca_script_common (SCRIPT_TYPE, SCRIPT_CLASS_NAME, MD5, UPDATE_TS, REMARKS) values ('@SCRIPT_TYPE', '@SCRIPT_CLASS_NAME', '@MD5', sysdate, '@SCRIPT_COMMON_REMARKS');
        
        dbms_output.put_line  ('1, @SCRIPT_CLASS_NAME added.');
        
    ELSE 
        
         SELECT md5 INTO CurrentScriptBodyVersion from b2b_edi_alpaca_script_common WHERE SCRIPT_CLASS_NAME = '@SCRIPT_CLASS_NAME';
     
        IF (CurrentScriptBodyVersion = '@MD5') THEN
        
            dbms_output.put_line  ('1, @SCRIPT_CLASS_NAME version is same, not action to script common, MD5 is (@MD5).');
        
        ELSE 
        
            update b2b_edi_alpaca_script_common set MD5 = '@MD5', update_ts=sysdate where SCRIPT_CLASS_NAME = '@SCRIPT_CLASS_NAME';
        
            dbms_output.put_line  ('1, @SCRIPT_CLASS_NAME updated to (@MD5), previous version is (' || CurrentScriptBodyVersion || '). ');
        
        END IF;
        
    END IF;
    
    
    SELECT count(md5) INTO CountScriptBody from b2b_edi_alpaca_script_body WHERE MD5 = '@MD5';
    
    IF CountScriptBody = 0 THEN
        
        INSERT INTO b2b_edi_alpaca_script_body (MD5, SCRIPT_NAME, SCRIPT, UPDATE_TS) values ('@MD5', '@SCRIPT_CLASS_NAME', @VARIABLE_64, sysdate);
        
        dbms_output.put_line  ('2, @MD5 (@SCRIPT_CLASS_NAME) added to script body.');
        
    ELSE 
        
        dbms_output.put_line  ('2, @MD5 exists in script body already with name (@SCRIPT_CLASS_NAME), not action on it.');
        
    END IF;
    
        
END;
/

COMMIT;

SELECT SCRIPT_TYPE, SCRIPT_CLASS_NAME, MD5, UPDATE_TS, REMARKS FROM b2b_edi_alpaca_script_common WHERE SCRIPT_CLASS_NAME = '@SCRIPT_CLASS_NAME';

