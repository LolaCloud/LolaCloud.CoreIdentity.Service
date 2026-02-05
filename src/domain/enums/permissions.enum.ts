export enum LolaPermissions {
    LOLA_ALL = 'lola::*',
    LCI_ALL = 'lci::*',
    LCI_OPERATOR_CREATE = 'lci::operator::create',
    LCI_OPERATOR_UPDATE = 'lci::operator::update',
    LCI_OPERATOR_DELETE = 'lci::operator::delete',
    LCI_OPERATOR_ALL = 'lci::operator::*',


    LR_ALL = 'lr::*',
    LR_ACCOUNT_CREATE = 'lr::account::create',
    LR_ACCOUNT_UPDATE = 'lr::account::update',
    LR_ACCOUNT_DELETE = 'lr::account:delete',
    LR_ACCOUNT_ALL = 'lr::account::*',

    LR_ZONE_ADD = 'lr::zone::add',
    LR_ZONE_REMOVE = 'lr::zone::remove',
    LR_ZONE_ALL = 'lr::zone::*',

    LR_RECORD_CREATE = 'lr::record::create',
    LR_RECORD_DELETE = 'lr::record::delete',
    LR_RECORD_UPDATE = 'lr::record::update',
    LR_RECORD_ALL = 'lr::record::*'
} 