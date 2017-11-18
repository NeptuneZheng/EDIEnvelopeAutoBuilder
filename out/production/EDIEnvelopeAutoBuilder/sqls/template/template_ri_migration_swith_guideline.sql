-- for groovy migration - switch guideline
-- TpId: @TP_ID
-- Msg Type Id: @MSG_TYPE_ID
-- Dir ID: @DIR_ID
-- New GuideLine ID: @NEW_GDL_ID

select * from b2b_tp_msg_cfg where tp_id = '@TP_ID' and msg_type_id = '@MSG_TYPE_ID' and dir_id='@DIR_ID';

update b2b_tp_msg_cfg set profile_sts_id = 'INACTIVE' where tp_id = '@TP_ID' and msg_type_id = '@MSG_TYPE_ID' and dir_id='@DIR_ID' and profile_sts_id='ACTIVE';

insert into B2B_TP_MSG_CFG (TP_ID, MSG_TYPE_ID, DIR_ID, MSG_GUIDELINE_ID, NEED_RECEIVE_ACK, NEED_PROC_ACK, TRANS_TYPE_ID, TP_MSG_CLASS_TYPE_ID, MSG_CFG_VERSION, PROFILE_STS_ID, CREATE_TS, CREATED_BY, SPLITABLE) values 
('@TP_ID', '@MSG_TYPE_ID', '@DIR_ID', '@NEW_GDL_ID', 'F', 'F', 'JMS', '0', '1.00', 'ACTIVE', SYSDATE, 'migration', 'N'); 

insert into B2B_MCI_DESTINATION_QUEUE (tp_id, msg_type_id, dir_id, msg_fmt_id, Msg_Guideline_Id, Destination_Queue, External_Party, Mci_Trans_Type_Id, reply_to_queue) 
select tp_id, msg_type_id, dir_id, msg_fmt_id, '@NEW_GDL_ID', Destination_Queue, External_Party, Mci_Trans_Type_Id, reply_to_queue from B2B_MCI_DESTINATION_QUEUE where tp_id='@TP_ID' and msg_type_id='@MSG_TYPE_ID' and dir_id='@DIR_ID';


commit;

select * from b2b_tp_msg_cfg where tp_id = '@TP_ID' and msg_type_id = '@MSG_TYPE_ID' and dir_id='@DIR_ID';

