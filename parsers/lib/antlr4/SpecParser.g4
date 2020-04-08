parser grammar SpecParser;

/**
 imported grammar rules
   langExpr
   langId
   langType
   langModifier
   langStatement
   startSpec - the rule entering the lexer into specification mode
   endSpec - the rule exiting the lexer from specification mode
 exported grammar rules for PVL
   valContractClause        - contract clause
   valStatement             - proof guiding statement
   valWithThen              - with/then statement to use given/yields ghost arguments
   valReserved              - reserved identifiers
 exported grammar rules for other languages
   valEmbedContract         - sequence of contract clauses embedded in sequence of comments
   valEmbedContractBlock    - sequence of contract clauses embedded in one comment
   valEmbedStatementBlock   - sequence of valStatements embedded in a comment
   valEmbedWithThenBlock    - with and/or then in one comment
   valEmbedWithThen         - with and/or then in a sequence of comments
 */

valExpressionList
    : langExpr
    | langExpr ',' valExpressionList
    ;

valLabelList
    : langId
    | langId ',' valLabelList
    ;

valContractClause
 : 'modifies' valExpressionList ';'
 | 'accessible' valExpressionList ';'
 | 'requires' langExpr ';'
 | 'ensures' langExpr ';'
 | 'given' langType langId ';'
 | 'yields' langType langId ';'
 | 'context_everywhere' langExpr ';'
 | 'context' langExpr ';'
 | 'loop_invariant' langExpr ';'
 ;

valBlock
 : '{' valStatement* '}'
 ;

valStatement
 : 'create' valBlock               // create a magic wand
 | 'qed' langExpr ';'
 | 'apply' langExpr ';'
 | 'use' langExpr ';'
 | 'create' langExpr ';'             // create empty history
 | 'create' langExpr ',' langExpr ';'   // create given future
 | 'destroy' langExpr ',' langExpr ';'  // destroy given
 | 'destroy' langExpr ';'           // destroy empty future
 | 'split' langExpr ',' langExpr ',' langExpr ',' langExpr ',' langExpr ';'
 | 'merge' langExpr ',' langExpr ',' langExpr ',' langExpr ',' langExpr ';'
 | 'choose' langExpr ',' langExpr ',' langExpr ',' langExpr ';'
 | 'fold' langExpr ';'
 | 'unfold' langExpr ';'
 | 'open' langExpr ';'
 | 'close' langExpr ';'
 | 'assert' langExpr ';'
 | 'assume' langExpr ';'
 | 'inhale' langExpr ';'
 | 'exhale' langExpr ';'
 | 'label' langId ';'
 | 'refute' langExpr ';'
 | 'witness' langExpr ';'
 | 'ghost' langStatement
 | 'send' langExpr 'to' Identifier ',' langExpr ';'
 | 'recv' langExpr 'from' Identifier ',' langExpr ';'
 | 'transfer' langExpr ';'
 | 'csl_subject' langExpr ';'
 | 'spec_ignore' '}'
 | 'spec_ignore' '{'
 | 'action' langExpr ',' langExpr ',' langExpr ',' langExpr ( ',' langExpr ',' langExpr )* ';'
 | 'atomic' '(' valLabelList? ')' langStatement
 ;

valWithThenMapping
 : langId '=' langExpr ';'
 ;

valWithThen
 : 'with' '{' valWithThenMapping* '}'
 | 'then' '{' valWithThenMapping* '}'
 ;

valPrimary
    : langType '{' valExpressionList? '}'
    | '[' langExpr ']' langExpr
    | '|' langExpr '|'
    | '\\unfolding' langExpr '\\in' langExpr
    | '(' langExpr '!' Identifier ')'
    | '(' langExpr '\\memberof' langExpr ')'
    | '['  langExpr '..' langExpr ')'
    | '*'
    | '\\current_thread'
    | '(' ('\\forall*'|'\\forall'|'\\exists')
        langType langId '=' langExpr '..' langExpr ';' langExpr ')'
    | '(' ('\\forall*'|'\\forall'|'\\exists')
        langType langId ';' langExpr ';' langExpr ')'
    | '(' '\\let' langType langId '=' langExpr ';' langExpr ')'
    | '(' '\\sum' langType langId ';' langExpr ';' langExpr ')'
    | '\\length' '(' langExpr ')'
    | '\\old' '(' langExpr ')'
    | '\\id' '(' langExpr ')'
    | '\\typeof' '(' langExpr ')'
    | '\\matrix' '(' langExpr ',' langExpr ',' langExpr ')'
    | '\\array'  '(' langExpr ',' langExpr ')'
    | '\\pointer' '(' langExpr ',' langExpr ',' langExpr ')'
    | '\\pointer_index' '(' langExpr ',' langExpr ',' langExpr ')'
    | '\\values' '(' langExpr ',' langExpr ',' langExpr ')'
    | '\\sum' '(' langExpr ',' langExpr ')'
    | '\\vcmp' '(' langExpr ',' langExpr ')'
    | '\\vrep' '(' langExpr ')'
    | '\\msum' '(' langExpr ',' langExpr ')'
    | '\\mcmp' '(' langExpr ',' langExpr ')'
    | '\\mrep' '(' langExpr ')'
    | 'Reducible' '(' langExpr ',' ('+' | Identifier ) ')'
    | langId ':' langExpr
    ;

valReserved
 : ('create' | 'action' | 'destroy' | 'send' | 'recv' | 'use' | 'open' | 'close'
        | 'atomic'  | 'from' | 'merge' | 'split' | 'process' | 'apply' | 'label')
 | '\\result'
 | '\\current_thread'
 | 'none' // No permission
 | 'write' // Full permission
 | 'read' // Any read permission
 | 'None' // The empty value of the option langType
 | 'empty' // The empty process in the context of Models
 ;

valType
 : ('resource' | 'process' | 'frac' | 'zfrac' | 'rational' | 'bool')
 | 'seq' '<' langType '>'
 ;

valDeclaration
 : valContractClause* valModifier* langType langId '(' valArgList? ')' valPredicateDef
 | 'axiom' langId '{' langExpr '==' langExpr '}'
 ;

valPredicateDef
 : ';'
 | '=' langExpr ';'
 ;

valModifier
 :('pure'
 | 'inline'
 | 'thread_local'
)| langModifier
 ;

valArgList
 : valArg
 | valArg ',' valArgList
 ;

valArg
 : langType langId
 ;

valEmbedContract: valEmbedContractBlock+;

valEmbedContractBlock
 : startSpec valContractClause* endSpec
 ;

valEmbedStatementBlock
 : startSpec valStatement* endSpec
 ;

valEmbedWithThenBlock
 : startSpec valWithThen* endSpec
 ;

valEmbedWithThen
 : valEmbedWithThenBlock+
 ;

valEmbedDeclarationBlock
 : startSpec valDeclaration* endSpec
 ;