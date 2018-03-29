package bean;

import bean.edi.EdifactEnvelopV2;
import bean.edi.X12EnvelopV2;

/**
 * Created by XIAOTR on 3/29/2018.
 */
public class BelugaDefinitionJsonSettings {
    public String recordDelimiter = null;
    public String elementDelimiter = null;
    public String subElementDelimiter = null;
    public String x12ReplacementChar = null;
    public String escapeChar = null;
    public String elementType = null;
    public String isSuppressEmptyNodes = null;
    public String isEdifact = null;
    public String isX12 = null;
    public String isFieldValueTrimRightSpace = null;
    public String isFieldValueTrimLeadingSpace = null;
    public String formatStringTxnCount = null;
    public String formatStringTxnCountMaxLength = null;
    public String formatStringEdiControlNumber = null;
    public String formatStringEdiControlNumberMaxLength = null;
    public String formatStringEdiControlNumberInTransaction = null;
    public String formatStringEdiControlNumberInTransactionMaxLength = null;
    public X12EnvelopV2 x12EnvelopV2 = null;
    public EdifactEnvelopV2 edifactEnvelopV2 = null;

    public BelugaDefinitionJsonSettings() {
    }

}
