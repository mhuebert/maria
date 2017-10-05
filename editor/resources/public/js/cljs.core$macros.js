goog.provide('cljs.core$macros');
goog.require('clojure.walk');
goog.require('clojure.set');
goog.require('clojure.string');
goog.require('cljs.compiler');
goog.require('cljs.env');
goog.require('cljs.core');
goog.require('cljs.analyzer');
var ret__18490__auto___6709 = (function (){
/**
 * Threads the expr through the forms. Inserts x as the
 *   second item in the first form, making a list of it if it is not a
 *   list already. If there are more forms, inserts the first form as the
 *   second item in second form, etc.
 */
cljs.core$macros.__GT_ = (function cljs$core$macros$__GT_(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6710 = arguments.length;
var i__18451__auto___6711 = (0);
while(true){
if((i__18451__auto___6711 < len__18450__auto___6710)){
args__18453__auto__.push((arguments[i__18451__auto___6711]));

var G__6712 = (i__18451__auto___6711 + (1));
i__18451__auto___6711 = G__6712;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.__GT_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.__GT_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,forms){
var x__$1 = x;
var forms__$1 = forms;
while(true){
if(cljs.core.truth_(forms__$1)){
var form = cljs.core.first(forms__$1);
var threaded = ((cljs.core.seq_QMARK_(form))?cljs.core.with_meta(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = cljs.core.first(form);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = x__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.next(form)], 0)))),cljs.core.meta(form)):(function (){var x__18265__auto__ = form;
return cljs.core._conj((function (){var x__18265__auto____$1 = x__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})());
var G__6713 = threaded;
var G__6714 = cljs.core.next(forms__$1);
x__$1 = G__6713;
forms__$1 = G__6714;
continue;
} else {
return x__$1;
}
break;
}
});

cljs.core$macros.__GT_.cljs$lang$maxFixedArity = (3);

cljs.core$macros.__GT_.cljs$lang$applyTo = (function (seq6705){
var G__6706 = cljs.core.first(seq6705);
var seq6705__$1 = cljs.core.next(seq6705);
var G__6707 = cljs.core.first(seq6705__$1);
var seq6705__$2 = cljs.core.next(seq6705__$1);
var G__6708 = cljs.core.first(seq6705__$2);
var seq6705__$3 = cljs.core.next(seq6705__$2);
return cljs.core$macros.__GT_.cljs$core$IFn$_invoke$arity$variadic(G__6706,G__6707,G__6708,seq6705__$3);
});

return null;
})()
;
cljs.core$macros.__GT_.cljs$lang$macro = true;

var ret__18490__auto___6719 = (function (){
/**
 * Threads the expr through the forms. Inserts x as the
 *   last item in the first form, making a list of it if it is not a
 *   list already. If there are more forms, inserts the first form as the
 *   last item in second form, etc.
 */
cljs.core$macros.__GT__GT_ = (function cljs$core$macros$__GT__GT_(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6720 = arguments.length;
var i__18451__auto___6721 = (0);
while(true){
if((i__18451__auto___6721 < len__18450__auto___6720)){
args__18453__auto__.push((arguments[i__18451__auto___6721]));

var G__6722 = (i__18451__auto___6721 + (1));
i__18451__auto___6721 = G__6722;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.__GT__GT_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.__GT__GT_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,forms){
var x__$1 = x;
var forms__$1 = forms;
while(true){
if(cljs.core.truth_(forms__$1)){
var form = cljs.core.first(forms__$1);
var threaded = ((cljs.core.seq_QMARK_(form))?cljs.core.with_meta(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = cljs.core.first(form);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.next(form),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = x__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),cljs.core.meta(form)):(function (){var x__18265__auto__ = form;
return cljs.core._conj((function (){var x__18265__auto____$1 = x__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})());
var G__6723 = threaded;
var G__6724 = cljs.core.next(forms__$1);
x__$1 = G__6723;
forms__$1 = G__6724;
continue;
} else {
return x__$1;
}
break;
}
});

cljs.core$macros.__GT__GT_.cljs$lang$maxFixedArity = (3);

cljs.core$macros.__GT__GT_.cljs$lang$applyTo = (function (seq6715){
var G__6716 = cljs.core.first(seq6715);
var seq6715__$1 = cljs.core.next(seq6715);
var G__6717 = cljs.core.first(seq6715__$1);
var seq6715__$2 = cljs.core.next(seq6715__$1);
var G__6718 = cljs.core.first(seq6715__$2);
var seq6715__$3 = cljs.core.next(seq6715__$2);
return cljs.core$macros.__GT__GT_.cljs$core$IFn$_invoke$arity$variadic(G__6716,G__6717,G__6718,seq6715__$3);
});

return null;
})()
;
cljs.core$macros.__GT__GT_.cljs$lang$macro = true;

var ret__18490__auto___6732 = (function (){
/**
 * form => fieldName-symbol or (instanceMethodName-symbol args*)
 * 
 *   Expands into a member access (.) of the first member on the first
 *   argument, followed by the next member on the result, etc. For
 *   instance:
 * 
 *   (.. System (getProperties) (get "os.name"))
 * 
 *   expands to:
 * 
 *   (. (. System (getProperties)) (get "os.name"))
 * 
 *   but is easier to write, read, and understand.
 */
cljs.core$macros._DOT__DOT_ = (function cljs$core$macros$_DOT__DOT_(var_args){
var G__6731 = arguments.length;
switch (G__6731) {
case 4:
return cljs.core$macros._DOT__DOT_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___6734 = arguments.length;
var i__18451__auto___6735 = (0);
while(true){
if((i__18451__auto___6735 < len__18450__auto___6734)){
args_arr__18469__auto__.push((arguments[i__18451__auto___6735]));

var G__6736 = (i__18451__auto___6735 + (1));
i__18451__auto___6735 = G__6736;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros._DOT__DOT_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros._DOT__DOT_.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,form){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = form;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros._DOT__DOT_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,form,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"..","..",-300507420,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = form;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
});

cljs.core$macros._DOT__DOT_.cljs$lang$applyTo = (function (seq6726){
var G__6727 = cljs.core.first(seq6726);
var seq6726__$1 = cljs.core.next(seq6726);
var G__6728 = cljs.core.first(seq6726__$1);
var seq6726__$2 = cljs.core.next(seq6726__$1);
var G__6729 = cljs.core.first(seq6726__$2);
var seq6726__$3 = cljs.core.next(seq6726__$2);
var G__6730 = cljs.core.first(seq6726__$3);
var seq6726__$4 = cljs.core.next(seq6726__$3);
return cljs.core$macros._DOT__DOT_.cljs$core$IFn$_invoke$arity$variadic(G__6727,G__6728,G__6729,G__6730,seq6726__$4);
});

cljs.core$macros._DOT__DOT_.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros._DOT__DOT_.cljs$lang$macro = true;

var ret__18490__auto___6740 = (function (){
/**
 * Ignores body, yields nil
 */
cljs.core$macros.comment = (function cljs$core$macros$comment(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6741 = arguments.length;
var i__18451__auto___6742 = (0);
while(true){
if((i__18451__auto___6742 < len__18450__auto___6741)){
args__18453__auto__.push((arguments[i__18451__auto___6742]));

var G__6743 = (i__18451__auto___6742 + (1));
i__18451__auto___6742 = G__6743;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.comment.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.comment.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,body){
return null;
});

cljs.core$macros.comment.cljs$lang$maxFixedArity = (2);

cljs.core$macros.comment.cljs$lang$applyTo = (function (seq6737){
var G__6738 = cljs.core.first(seq6737);
var seq6737__$1 = cljs.core.next(seq6737);
var G__6739 = cljs.core.first(seq6737__$1);
var seq6737__$2 = cljs.core.next(seq6737__$1);
return cljs.core$macros.comment.cljs$core$IFn$_invoke$arity$variadic(G__6738,G__6739,seq6737__$2);
});

return null;
})()
;
cljs.core$macros.comment.cljs$lang$macro = true;

var ret__18490__auto___6747 = (function (){
/**
 * Takes a set of test/expr pairs. It evaluates each test one at a
 *   time.  If a test returns logical true, cond evaluates and returns
 *   the value of the corresponding expr and doesn't evaluate any of the
 *   other tests or exprs. (cond) returns nil.
 */
cljs.core$macros.cond = (function cljs$core$macros$cond(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6748 = arguments.length;
var i__18451__auto___6749 = (0);
while(true){
if((i__18451__auto___6749 < len__18450__auto___6748)){
args__18453__auto__.push((arguments[i__18451__auto___6749]));

var G__6750 = (i__18451__auto___6749 + (1));
i__18451__auto___6749 = G__6750;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.cond.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.cond.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,clauses){
if(cljs.core.truth_(clauses)){
return cljs.core._conj((function (){var x__18265__auto__ = cljs.core.first(clauses);
return cljs.core._conj((function (){var x__18265__auto____$1 = ((cljs.core.next(clauses))?cljs.core.second(clauses):(function(){throw (new Error("cond requires an even number of forms"))})());
return cljs.core._conj((function (){var x__18265__auto____$2 = cljs.core.cons(new cljs.core.Symbol("cljs.core","cond","cljs.core/cond",2005388338,null),cljs.core.next(cljs.core.next(clauses)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$2);
})(),x__18265__auto____$1);
})(),x__18265__auto__);
})(),new cljs.core.Symbol(null,"if","if",1181717262,null));
} else {
return null;
}
});

cljs.core$macros.cond.cljs$lang$maxFixedArity = (2);

cljs.core$macros.cond.cljs$lang$applyTo = (function (seq6744){
var G__6745 = cljs.core.first(seq6744);
var seq6744__$1 = cljs.core.next(seq6744);
var G__6746 = cljs.core.first(seq6744__$1);
var seq6744__$2 = cljs.core.next(seq6744__$1);
return cljs.core$macros.cond.cljs$core$IFn$_invoke$arity$variadic(G__6745,G__6746,seq6744__$2);
});

return null;
})()
;
cljs.core$macros.cond.cljs$lang$macro = true;

var ret__18490__auto___6755 = (function (){
/**
 * defs the supplied var names with no bindings, useful for making forward declarations.
 */
cljs.core$macros.declare = (function cljs$core$macros$declare(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6756 = arguments.length;
var i__18451__auto___6757 = (0);
while(true){
if((i__18451__auto___6757 < len__18450__auto___6756)){
args__18453__auto__.push((arguments[i__18451__auto___6757]));

var G__6758 = (i__18451__auto___6757 + (1));
i__18451__auto___6757 = G__6758;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.declare.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.declare.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,names){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__6751_SHARP_){
return cljs.core._conj((function (){var x__18265__auto__ = cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(p1__6751_SHARP_,cljs.core.assoc,new cljs.core.Keyword(null,"declared","declared",92336021),true);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),new cljs.core.Symbol(null,"def","def",597100991,null));
}),names))));
});

cljs.core$macros.declare.cljs$lang$maxFixedArity = (2);

cljs.core$macros.declare.cljs$lang$applyTo = (function (seq6752){
var G__6753 = cljs.core.first(seq6752);
var seq6752__$1 = cljs.core.next(seq6752);
var G__6754 = cljs.core.first(seq6752__$1);
var seq6752__$2 = cljs.core.next(seq6752__$1);
return cljs.core$macros.declare.cljs$core$IFn$_invoke$arity$variadic(G__6753,G__6754,seq6752__$2);
});

return null;
})()
;
cljs.core$macros.declare.cljs$lang$macro = true;

var ret__18490__auto___6763 = (function (){
/**
 * Evaluates x then calls all of the methods and functions with the
 *   value of x supplied at the front of the given arguments.  The forms
 *   are evaluated in order.  Returns x.
 * 
 *   (doto (new java.util.HashMap) (.put "a" 1) (.put "b" 2))
 */
cljs.core$macros.doto = (function cljs$core$macros$doto(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6764 = arguments.length;
var i__18451__auto___6765 = (0);
while(true){
if((i__18451__auto___6765 < len__18450__auto___6764)){
args__18453__auto__.push((arguments[i__18451__auto___6765]));

var G__6766 = (i__18451__auto___6765 + (1));
i__18451__auto___6765 = G__6766;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.doto.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.doto.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,forms){
var gx = cljs.core.gensym.cljs$core$IFn$_invoke$arity$0();
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = gx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (gx){
return (function (f){
if(cljs.core.seq_QMARK_(f)){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = cljs.core.first(f);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = gx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.next(f)], 0))));
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = f;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = gx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
}
});})(gx))
,forms),(function (){var x__18265__auto__ = gx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.doto.cljs$lang$maxFixedArity = (3);

cljs.core$macros.doto.cljs$lang$applyTo = (function (seq6759){
var G__6760 = cljs.core.first(seq6759);
var seq6759__$1 = cljs.core.next(seq6759);
var G__6761 = cljs.core.first(seq6759__$1);
var seq6759__$2 = cljs.core.next(seq6759__$1);
var G__6762 = cljs.core.first(seq6759__$2);
var seq6759__$3 = cljs.core.next(seq6759__$2);
return cljs.core$macros.doto.cljs$core$IFn$_invoke$arity$variadic(G__6760,G__6761,G__6762,seq6759__$3);
});

return null;
})()
;
cljs.core$macros.doto.cljs$lang$macro = true;

cljs.core$macros.parse_impls = (function cljs$core$macros$parse_impls(specs){
var ret = cljs.core.PersistentArrayMap.EMPTY;
var s = specs;
while(true){
if(cljs.core.seq(s)){
var G__6767 = cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(ret,cljs.core.first(s),cljs.core.take_while.cljs$core$IFn$_invoke$arity$2(cljs.core.seq_QMARK_,cljs.core.next(s)));
var G__6768 = cljs.core.drop_while.cljs$core$IFn$_invoke$arity$2(cljs.core.seq_QMARK_,cljs.core.next(s));
ret = G__6767;
s = G__6768;
continue;
} else {
return ret;
}
break;
}
});
cljs.core$macros.emit_extend_protocol = (function cljs$core$macros$emit_extend_protocol(p,specs){
var impls = cljs.core$macros.parse_impls(specs);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (impls){
return (function (p__6769){
var vec__6770 = p__6769;
var t = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__6770,(0),null);
var fs = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__6770,(1),null);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","extend-type","cljs.core$macros/extend-type",1713295201,null)),(function (){var x__18265__auto__ = t;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = p;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),fs], 0))));
});})(impls))
,impls))));
});
var ret__18490__auto___6777 = (function (){
/**
 * Useful when you want to provide several implementations of the same
 *   protocol all at once. Takes a single protocol and the implementation
 *   of that protocol for one or more types. Expands into calls to
 *   extend-type:
 * 
 *   (extend-protocol Protocol
 *     AType
 *       (foo [x] ...)
 *       (bar [x y] ...)
 *     BType
 *       (foo [x] ...)
 *       (bar [x y] ...)
 *     AClass
 *       (foo [x] ...)
 *       (bar [x y] ...)
 *     nil
 *       (foo [x] ...)
 *       (bar [x y] ...))
 * 
 *   expands into:
 * 
 *   (do
 *    (clojure.core/extend-type AType Protocol
 *      (foo [x] ...)
 *      (bar [x y] ...))
 *    (clojure.core/extend-type BType Protocol
 *      (foo [x] ...)
 *      (bar [x y] ...))
 *    (clojure.core/extend-type AClass Protocol
 *      (foo [x] ...)
 *      (bar [x y] ...))
 *    (clojure.core/extend-type nil Protocol
 *      (foo [x] ...)
 *      (bar [x y] ...)))
 */
cljs.core$macros.extend_protocol = (function cljs$core$macros$extend_protocol(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6778 = arguments.length;
var i__18451__auto___6779 = (0);
while(true){
if((i__18451__auto___6779 < len__18450__auto___6778)){
args__18453__auto__.push((arguments[i__18451__auto___6779]));

var G__6780 = (i__18451__auto___6779 + (1));
i__18451__auto___6779 = G__6780;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.extend_protocol.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.extend_protocol.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,p,specs){
return cljs.core$macros.emit_extend_protocol(p,specs);
});

cljs.core$macros.extend_protocol.cljs$lang$maxFixedArity = (3);

cljs.core$macros.extend_protocol.cljs$lang$applyTo = (function (seq6773){
var G__6774 = cljs.core.first(seq6773);
var seq6773__$1 = cljs.core.next(seq6773);
var G__6775 = cljs.core.first(seq6773__$1);
var seq6773__$2 = cljs.core.next(seq6773__$1);
var G__6776 = cljs.core.first(seq6773__$2);
var seq6773__$3 = cljs.core.next(seq6773__$2);
return cljs.core$macros.extend_protocol.cljs$core$IFn$_invoke$arity$variadic(G__6774,G__6775,G__6776,seq6773__$3);
});

return null;
})()
;
cljs.core$macros.extend_protocol.cljs$lang$macro = true;

cljs.core$macros.maybe_destructured = (function cljs$core$macros$maybe_destructured(params,body){
if(cljs.core.every_QMARK_(cljs.core.symbol_QMARK_,params)){
return cljs.core.cons(params,body);
} else {
var params__$1 = params;
var new_params = cljs.core.with_meta(cljs.core.PersistentVector.EMPTY,cljs.core.meta(params__$1));
var lets = cljs.core.PersistentVector.EMPTY;
while(true){
if(cljs.core.truth_(params__$1)){
if((cljs.core.first(params__$1) instanceof cljs.core.Symbol)){
var G__6781 = cljs.core.next(params__$1);
var G__6782 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(new_params,cljs.core.first(params__$1));
var G__6783 = lets;
params__$1 = G__6781;
new_params = G__6782;
lets = G__6783;
continue;
} else {
var gparam = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("p__");
var G__6784 = cljs.core.next(params__$1);
var G__6785 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(new_params,gparam);
var G__6786 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(cljs.core.conj.cljs$core$IFn$_invoke$arity$2(lets,cljs.core.first(params__$1)),gparam);
params__$1 = G__6784;
new_params = G__6785;
lets = G__6786;
continue;
}
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = new_params;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = lets;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
}
break;
}
}
});
var ret__18490__auto___6793 = (function (){
/**
 * params => positional-params* , or positional-params* & next-param
 *   positional-param => binding-form
 *   next-param => binding-form
 *   name => symbol
 * 
 *   Defines a function
 */
cljs.core$macros.fn = (function cljs$core$macros$fn(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6794 = arguments.length;
var i__18451__auto___6795 = (0);
while(true){
if((i__18451__auto___6795 < len__18450__auto___6794)){
args__18453__auto__.push((arguments[i__18451__auto___6795]));

var G__6796 = (i__18451__auto___6795 + (1));
i__18451__auto___6795 = G__6796;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.fn.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.fn.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,sigs){
var name = (((cljs.core.first(sigs) instanceof cljs.core.Symbol))?cljs.core.first(sigs):null);
var sigs__$1 = (cljs.core.truth_(name)?cljs.core.next(sigs):sigs);
var sigs__$2 = ((cljs.core.vector_QMARK_(cljs.core.first(sigs__$1)))?(function (){var x__18265__auto__ = sigs__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})():((cljs.core.seq_QMARK_(cljs.core.first(sigs__$1)))?sigs__$1:(function(){throw (new Error(((cljs.core.seq(sigs__$1))?["Parameter declaration ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.first(sigs__$1))," should be a vector"].join(''):["Parameter declaration missing"].join(''))))})()));
var psig = ((function (name,sigs__$1,sigs__$2){
return (function (sig){
if(!(cljs.core.seq_QMARK_(sig))){
throw (new Error(["Invalid signature ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(sig)," should be a list"].join('')));
} else {
}

var vec__6790 = sig;
var seq__6791 = cljs.core.seq(vec__6790);
var first__6792 = cljs.core.first(seq__6791);
var seq__6791__$1 = cljs.core.next(seq__6791);
var params = first__6792;
var body = seq__6791__$1;
var _ = ((!(cljs.core.vector_QMARK_(params)))?(function(){throw (new Error(((cljs.core.seq_QMARK_(cljs.core.first(sigs__$2)))?["Parameter declaration ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(params)," should be a vector"].join(''):["Invalid signature ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(sig)," should be a list"].join(''))))})():null);
var conds = (((cljs.core.next(body)) && (cljs.core.map_QMARK_(cljs.core.first(body))))?cljs.core.first(body):null);
var body__$1 = (cljs.core.truth_(conds)?cljs.core.next(body):body);
var conds__$1 = (function (){var or__17767__auto__ = conds;
if(cljs.core.truth_(or__17767__auto__)){
return or__17767__auto__;
} else {
return cljs.core.meta(params);
}
})();
var pre = new cljs.core.Keyword(null,"pre","pre",2118456869).cljs$core$IFn$_invoke$arity$1(conds__$1);
var post = new cljs.core.Keyword(null,"post","post",269697687).cljs$core$IFn$_invoke$arity$1(conds__$1);
var body__$2 = (cljs.core.truth_(post)?cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1((function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"%","%",-950237169,null)),(function (){var x__18265__auto__ = ((((1) < cljs.core.count(body__$1)))?cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),body__$1))):cljs.core.first(body__$1));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (vec__6790,seq__6791,first__6792,seq__6791__$1,params,body,_,conds,body__$1,conds__$1,pre,post,name,sigs__$1,sigs__$2){
return (function (c){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","assert","cljs.core$macros/assert",1333198789,null)),(function (){var x__18265__auto__ = c;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});})(vec__6790,seq__6791,first__6792,seq__6791__$1,params,body,_,conds,body__$1,conds__$1,pre,post,name,sigs__$1,sigs__$2))
,post),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"%","%",-950237169,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))):body__$1);
var body__$3 = (cljs.core.truth_(pre)?cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (vec__6790,seq__6791,first__6792,seq__6791__$1,params,body,_,conds,body__$1,conds__$1,pre,post,body__$2,name,sigs__$1,sigs__$2){
return (function (c){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","assert","cljs.core$macros/assert",1333198789,null)),(function (){var x__18265__auto__ = c;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});})(vec__6790,seq__6791,first__6792,seq__6791__$1,params,body,_,conds,body__$1,conds__$1,pre,post,body__$2,name,sigs__$1,sigs__$2))
,pre),body__$2):body__$2);
return cljs.core$macros.maybe_destructured(params,body__$3);
});})(name,sigs__$1,sigs__$2))
;
var new_sigs = cljs.core.map.cljs$core$IFn$_invoke$arity$2(psig,sigs__$2);
return cljs.core.with_meta((cljs.core.truth_(name)?cljs.core.list_STAR_.cljs$core$IFn$_invoke$arity$3(new cljs.core.Symbol(null,"fn*","fn*",-752876845,null),name,new_sigs):cljs.core.cons(new cljs.core.Symbol(null,"fn*","fn*",-752876845,null),new_sigs)),cljs.core.meta(_AMPERSAND_form));
});

cljs.core$macros.fn.cljs$lang$maxFixedArity = (2);

cljs.core$macros.fn.cljs$lang$applyTo = (function (seq6787){
var G__6788 = cljs.core.first(seq6787);
var seq6787__$1 = cljs.core.next(seq6787);
var G__6789 = cljs.core.first(seq6787__$1);
var seq6787__$2 = cljs.core.next(seq6787__$1);
return cljs.core$macros.fn.cljs$core$IFn$_invoke$arity$variadic(G__6788,G__6789,seq6787__$2);
});

return null;
})()
;
cljs.core$macros.fn.cljs$lang$macro = true;

var ret__18490__auto___6801 = (function (){
/**
 * same as defn, yielding non-public def
 */
cljs.core$macros.defn_ = (function cljs$core$macros$defn_(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6802 = arguments.length;
var i__18451__auto___6803 = (0);
while(true){
if((i__18451__auto___6803 < len__18450__auto___6802)){
args__18453__auto__.push((arguments[i__18451__auto___6803]));

var G__6804 = (i__18451__auto___6803 + (1));
i__18451__auto___6803 = G__6804;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.defn_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.defn_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,name,decls){
return cljs.core.list_STAR_.cljs$core$IFn$_invoke$arity$3(new cljs.core.Symbol("cljs.core$macros","defn","cljs.core$macros/defn",-728332354,null),cljs.core.with_meta(name,cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.meta(name),new cljs.core.Keyword(null,"private","private",-558947994),true)),decls);
});

cljs.core$macros.defn_.cljs$lang$maxFixedArity = (3);

cljs.core$macros.defn_.cljs$lang$applyTo = (function (seq6797){
var G__6798 = cljs.core.first(seq6797);
var seq6797__$1 = cljs.core.next(seq6797);
var G__6799 = cljs.core.first(seq6797__$1);
var seq6797__$2 = cljs.core.next(seq6797__$1);
var G__6800 = cljs.core.first(seq6797__$2);
var seq6797__$3 = cljs.core.next(seq6797__$2);
return cljs.core$macros.defn_.cljs$core$IFn$_invoke$arity$variadic(G__6798,G__6799,G__6800,seq6797__$3);
});

return null;
})()
;
cljs.core$macros.defn_.cljs$lang$macro = true;

var ret__18490__auto___6814 = (function (){
/**
 * bindings => binding-form test
 * 
 *   If test is true, evaluates then with binding-form bound to the value of
 *   test, if not, yields else
 */
cljs.core$macros.if_let = (function cljs$core$macros$if_let(var_args){
var G__6813 = arguments.length;
switch (G__6813) {
case 4:
return cljs.core$macros.if_let.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___6816 = arguments.length;
var i__18451__auto___6817 = (0);
while(true){
if((i__18451__auto___6817 < len__18450__auto___6816)){
args_arr__18469__auto__.push((arguments[i__18451__auto___6817]));

var G__6818 = (i__18451__auto___6817 + (1));
i__18451__auto___6817 = G__6818;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((5)),(0),null));
return cljs.core$macros.if_let.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),argseq__18470__auto__);

}
});

cljs.core$macros.if_let.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,then){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","if-let","cljs.core$macros/if-let",1291543946,null)),(function (){var x__18265__auto__ = bindings;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = then;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
});

cljs.core$macros.if_let.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,then,else$,oldform){
if(cljs.core.vector_QMARK_(bindings)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("if-let requires a vector for its binding",cljs.core.PersistentArrayMap.EMPTY);
}

if(cljs.core.empty_QMARK_(oldform)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("if-let requires 1 or 2 forms after binding vector",cljs.core.PersistentArrayMap.EMPTY);
}

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((2),cljs.core.count(bindings))){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("if-let requires exactly 2 forms in binding vector",cljs.core.PersistentArrayMap.EMPTY);
}


var form = (bindings.cljs$core$IFn$_invoke$arity$1 ? bindings.cljs$core$IFn$_invoke$arity$1((0)) : bindings.call(null,(0)));
var tst = (bindings.cljs$core$IFn$_invoke$arity$1 ? bindings.cljs$core$IFn$_invoke$arity$1((1)) : bindings.call(null,(1)));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"temp__6805__auto__","temp__6805__auto__",1737386808,null)),(function (){var x__18265__auto__ = tst;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"temp__6805__auto__","temp__6805__auto__",1737386808,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = form;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"temp__6805__auto__","temp__6805__auto__",1737386808,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = then;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = else$;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.if_let.cljs$lang$applyTo = (function (seq6807){
var G__6808 = cljs.core.first(seq6807);
var seq6807__$1 = cljs.core.next(seq6807);
var G__6809 = cljs.core.first(seq6807__$1);
var seq6807__$2 = cljs.core.next(seq6807__$1);
var G__6810 = cljs.core.first(seq6807__$2);
var seq6807__$3 = cljs.core.next(seq6807__$2);
var G__6811 = cljs.core.first(seq6807__$3);
var seq6807__$4 = cljs.core.next(seq6807__$3);
var G__6812 = cljs.core.first(seq6807__$4);
var seq6807__$5 = cljs.core.next(seq6807__$4);
return cljs.core$macros.if_let.cljs$core$IFn$_invoke$arity$variadic(G__6808,G__6809,G__6810,G__6811,G__6812,seq6807__$5);
});

cljs.core$macros.if_let.cljs$lang$maxFixedArity = (5);

return null;
})()
;
cljs.core$macros.if_let.cljs$lang$macro = true;

var ret__18490__auto___6821 = (function (){
/**
 * Evaluates test. If logical false, evaluates and returns then expr,
 *   otherwise else expr, if supplied, else nil.
 */
cljs.core$macros.if_not = (function cljs$core$macros$if_not(var_args){
var G__6820 = arguments.length;
switch (G__6820) {
case 4:
return cljs.core$macros.if_not.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 5:
return cljs.core$macros.if_not.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1((arguments.length - (2)))].join('')));

}
});

cljs.core$macros.if_not.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,test,then){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","if-not","cljs.core$macros/if-not",-1825285737,null)),(function (){var x__18265__auto__ = test;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = then;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
});

cljs.core$macros.if_not.cljs$core$IFn$_invoke$arity$5 = (function (_AMPERSAND_form,_AMPERSAND_env,test,then,else$){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","not","cljs.core$macros/not",325607907,null)),(function (){var x__18265__auto__ = test;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = then;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = else$;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.if_not.cljs$lang$maxFixedArity = 5;

return null;
})()
;
cljs.core$macros.if_not.cljs$lang$macro = true;

var ret__18490__auto___6828 = (function (){
/**
 * fnspec ==> (fname [params*] exprs) or (fname ([params*] exprs)+)
 * 
 *   Takes a vector of function specs and a body, and generates a set of
 *   bindings of functions to their names. All of the names are available
 *   in all of the definitions of the functions, as well as the body.
 */
cljs.core$macros.letfn = (function cljs$core$macros$letfn(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6829 = arguments.length;
var i__18451__auto___6830 = (0);
while(true){
if((i__18451__auto___6830 < len__18450__auto___6829)){
args__18453__auto__.push((arguments[i__18451__auto___6830]));

var G__6831 = (i__18451__auto___6830 + (1));
i__18451__auto___6830 = G__6831;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.letfn.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.letfn.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,fnspecs,body){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"letfn*","letfn*",-110097810,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.interleave.cljs$core$IFn$_invoke$arity$2(cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.first,fnspecs),cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__6823_SHARP_){
return cljs.core.cons(new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null),p1__6823_SHARP_);
}),fnspecs)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
});

cljs.core$macros.letfn.cljs$lang$maxFixedArity = (3);

cljs.core$macros.letfn.cljs$lang$applyTo = (function (seq6824){
var G__6825 = cljs.core.first(seq6824);
var seq6824__$1 = cljs.core.next(seq6824);
var G__6826 = cljs.core.first(seq6824__$1);
var seq6824__$2 = cljs.core.next(seq6824__$1);
var G__6827 = cljs.core.first(seq6824__$2);
var seq6824__$3 = cljs.core.next(seq6824__$2);
return cljs.core$macros.letfn.cljs$core$IFn$_invoke$arity$variadic(G__6825,G__6826,G__6827,seq6824__$3);
});

return null;
})()
;
cljs.core$macros.letfn.cljs$lang$macro = true;

var ret__18490__auto___6836 = (function (){
/**
 * Expands into code that creates a fn that expects to be passed an
 *   object and any args and calls the named instance method on the
 *   object passing the args. Use when you want to treat a JavaScript
 *   method as a first-class fn.
 */
cljs.core$macros.memfn = (function cljs$core$macros$memfn(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6837 = arguments.length;
var i__18451__auto___6838 = (0);
while(true){
if((i__18451__auto___6838 < len__18450__auto___6837)){
args__18453__auto__.push((arguments[i__18451__auto___6838]));

var G__6839 = (i__18451__auto___6838 + (1));
i__18451__auto___6838 = G__6839;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.memfn.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.memfn.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,name,args){
var t = cljs.core.with_meta(cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("target"),cljs.core.meta(name));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = t;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),args))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = t;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = name;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),args)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.memfn.cljs$lang$maxFixedArity = (3);

cljs.core$macros.memfn.cljs$lang$applyTo = (function (seq6832){
var G__6833 = cljs.core.first(seq6832);
var seq6832__$1 = cljs.core.next(seq6832);
var G__6834 = cljs.core.first(seq6832__$1);
var seq6832__$2 = cljs.core.next(seq6832__$1);
var G__6835 = cljs.core.first(seq6832__$2);
var seq6832__$3 = cljs.core.next(seq6832__$2);
return cljs.core$macros.memfn.cljs$core$IFn$_invoke$arity$variadic(G__6833,G__6834,G__6835,seq6832__$3);
});

return null;
})()
;
cljs.core$macros.memfn.cljs$lang$macro = true;

var ret__18490__auto___6844 = (function (){
/**
 * Evaluates test. If logical true, evaluates body in an implicit do.
 */
cljs.core$macros.when = (function cljs$core$macros$when(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6845 = arguments.length;
var i__18451__auto___6846 = (0);
while(true){
if((i__18451__auto___6846 < len__18450__auto___6845)){
args__18453__auto__.push((arguments[i__18451__auto___6846]));

var G__6847 = (i__18451__auto___6846 + (1));
i__18451__auto___6846 = G__6847;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.when.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.when.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,test,body){
return cljs.core._conj((function (){var x__18265__auto__ = test;
return cljs.core._conj((function (){var x__18265__auto____$1 = cljs.core.cons(new cljs.core.Symbol(null,"do","do",1686842252,null),body);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),new cljs.core.Symbol(null,"if","if",1181717262,null));
});

cljs.core$macros.when.cljs$lang$maxFixedArity = (3);

cljs.core$macros.when.cljs$lang$applyTo = (function (seq6840){
var G__6841 = cljs.core.first(seq6840);
var seq6840__$1 = cljs.core.next(seq6840);
var G__6842 = cljs.core.first(seq6840__$1);
var seq6840__$2 = cljs.core.next(seq6840__$1);
var G__6843 = cljs.core.first(seq6840__$2);
var seq6840__$3 = cljs.core.next(seq6840__$2);
return cljs.core$macros.when.cljs$core$IFn$_invoke$arity$variadic(G__6841,G__6842,G__6843,seq6840__$3);
});

return null;
})()
;
cljs.core$macros.when.cljs$lang$macro = true;

var ret__18490__auto___6856 = (function (){
/**
 * bindings => x xs
 * 
 *   Roughly the same as (when (seq xs) (let [x (first xs)] body)) but xs is evaluated only once
 */
cljs.core$macros.when_first = (function cljs$core$macros$when_first(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6857 = arguments.length;
var i__18451__auto___6858 = (0);
while(true){
if((i__18451__auto___6858 < len__18450__auto___6857)){
args__18453__auto__.push((arguments[i__18451__auto___6858]));

var G__6859 = (i__18451__auto___6858 + (1));
i__18451__auto___6858 = G__6859;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.when_first.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.when_first.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,body){
if(cljs.core.vector_QMARK_(bindings)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("when-first requires a vector for its binding",cljs.core.PersistentArrayMap.EMPTY);
}

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((2),cljs.core.count(bindings))){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("when-first requires exactly 2 forms in binding vector",cljs.core.PersistentArrayMap.EMPTY);
}


var vec__6853 = bindings;
var x = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__6853,(0),null);
var xs = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__6853,(1),null);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when-let","cljs.core$macros/when-let",-2004472648,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"xs__6848__auto__","xs__6848__auto__",-378453653,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","seq","cljs.core$macros/seq",-1873195024,null)),(function (){var x__18265__auto__ = xs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","first","cljs.core$macros/first",-999127023,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"xs__6848__auto__","xs__6848__auto__",-378453653,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.when_first.cljs$lang$maxFixedArity = (3);

cljs.core$macros.when_first.cljs$lang$applyTo = (function (seq6849){
var G__6850 = cljs.core.first(seq6849);
var seq6849__$1 = cljs.core.next(seq6849);
var G__6851 = cljs.core.first(seq6849__$1);
var seq6849__$2 = cljs.core.next(seq6849__$1);
var G__6852 = cljs.core.first(seq6849__$2);
var seq6849__$3 = cljs.core.next(seq6849__$2);
return cljs.core$macros.when_first.cljs$core$IFn$_invoke$arity$variadic(G__6850,G__6851,G__6852,seq6849__$3);
});

return null;
})()
;
cljs.core$macros.when_first.cljs$lang$macro = true;

var ret__18490__auto___6865 = (function (){
/**
 * bindings => binding-form test
 * 
 *   When test is true, evaluates body with binding-form bound to the value of test
 */
cljs.core$macros.when_let = (function cljs$core$macros$when_let(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6866 = arguments.length;
var i__18451__auto___6867 = (0);
while(true){
if((i__18451__auto___6867 < len__18450__auto___6866)){
args__18453__auto__.push((arguments[i__18451__auto___6867]));

var G__6868 = (i__18451__auto___6867 + (1));
i__18451__auto___6867 = G__6868;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.when_let.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.when_let.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,body){
if(cljs.core.vector_QMARK_(bindings)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("when-let requires a vector for its binding",cljs.core.PersistentArrayMap.EMPTY);
}

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((2),cljs.core.count(bindings))){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("when-let requires exactly 2 forms in binding vector",cljs.core.PersistentArrayMap.EMPTY);
}


var form = (bindings.cljs$core$IFn$_invoke$arity$1 ? bindings.cljs$core$IFn$_invoke$arity$1((0)) : bindings.call(null,(0)));
var tst = (bindings.cljs$core$IFn$_invoke$arity$1 ? bindings.cljs$core$IFn$_invoke$arity$1((1)) : bindings.call(null,(1)));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"temp__6860__auto__","temp__6860__auto__",360094729,null)),(function (){var x__18265__auto__ = tst;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when","cljs.core$macros/when",328457725,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"temp__6860__auto__","temp__6860__auto__",360094729,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = form;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"temp__6860__auto__","temp__6860__auto__",360094729,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.when_let.cljs$lang$maxFixedArity = (3);

cljs.core$macros.when_let.cljs$lang$applyTo = (function (seq6861){
var G__6862 = cljs.core.first(seq6861);
var seq6861__$1 = cljs.core.next(seq6861);
var G__6863 = cljs.core.first(seq6861__$1);
var seq6861__$2 = cljs.core.next(seq6861__$1);
var G__6864 = cljs.core.first(seq6861__$2);
var seq6861__$3 = cljs.core.next(seq6861__$2);
return cljs.core$macros.when_let.cljs$core$IFn$_invoke$arity$variadic(G__6862,G__6863,G__6864,seq6861__$3);
});

return null;
})()
;
cljs.core$macros.when_let.cljs$lang$macro = true;

var ret__18490__auto___6873 = (function (){
/**
 * Evaluates test. If logical false, evaluates body in an implicit do.
 */
cljs.core$macros.when_not = (function cljs$core$macros$when_not(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6874 = arguments.length;
var i__18451__auto___6875 = (0);
while(true){
if((i__18451__auto___6875 < len__18450__auto___6874)){
args__18453__auto__.push((arguments[i__18451__auto___6875]));

var G__6876 = (i__18451__auto___6875 + (1));
i__18451__auto___6875 = G__6876;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.when_not.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.when_not.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,test,body){
return cljs.core._conj((function (){var x__18265__auto__ = test;
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto____$1 = cljs.core.cons(new cljs.core.Symbol(null,"do","do",1686842252,null),body);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),null),x__18265__auto__);
})(),new cljs.core.Symbol(null,"if","if",1181717262,null));
});

cljs.core$macros.when_not.cljs$lang$maxFixedArity = (3);

cljs.core$macros.when_not.cljs$lang$applyTo = (function (seq6869){
var G__6870 = cljs.core.first(seq6869);
var seq6869__$1 = cljs.core.next(seq6869);
var G__6871 = cljs.core.first(seq6869__$1);
var seq6869__$2 = cljs.core.next(seq6869__$1);
var G__6872 = cljs.core.first(seq6869__$2);
var seq6869__$3 = cljs.core.next(seq6869__$2);
return cljs.core$macros.when_not.cljs$core$IFn$_invoke$arity$variadic(G__6870,G__6871,G__6872,seq6869__$3);
});

return null;
})()
;
cljs.core$macros.when_not.cljs$lang$macro = true;

var ret__18490__auto___6881 = (function (){
/**
 * Repeatedly executes body while test expression is true. Presumes
 *   some side-effect will cause test to become false/nil. Returns nil
 */
cljs.core$macros.while$ = (function cljs$core$macros$while(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6882 = arguments.length;
var i__18451__auto___6883 = (0);
while(true){
if((i__18451__auto___6883 < len__18450__auto___6882)){
args__18453__auto__.push((arguments[i__18451__auto___6883]));

var G__6884 = (i__18451__auto___6883 + (1));
i__18451__auto___6883 = G__6884;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.while$.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.while$.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,test,body){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","loop","cljs.core$macros/loop",1731108390,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when","cljs.core$macros/when",328457725,null)),(function (){var x__18265__auto__ = test;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body,(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"recur","recur",1202958259,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.while$.cljs$lang$maxFixedArity = (3);

cljs.core$macros.while$.cljs$lang$applyTo = (function (seq6877){
var G__6878 = cljs.core.first(seq6877);
var seq6877__$1 = cljs.core.next(seq6877);
var G__6879 = cljs.core.first(seq6877__$1);
var seq6877__$2 = cljs.core.next(seq6877__$1);
var G__6880 = cljs.core.first(seq6877__$2);
var seq6877__$3 = cljs.core.next(seq6877__$2);
return cljs.core$macros.while$.cljs$core$IFn$_invoke$arity$variadic(G__6878,G__6879,G__6880,seq6877__$3);
});

return null;
})()
;
cljs.core$macros.while$.cljs$lang$macro = true;

var ret__18490__auto___6893 = (function (){
/**
 * Takes an expression and a set of test/form pairs. Threads expr (via ->)
 *   through each form for which the corresponding test
 *   expression is true. Note that, unlike cond branching, cond-> threading does
 *   not short circuit after the first true test expression.
 */
cljs.core$macros.cond__GT_ = (function cljs$core$macros$cond__GT_(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6894 = arguments.length;
var i__18451__auto___6895 = (0);
while(true){
if((i__18451__auto___6895 < len__18450__auto___6894)){
args__18453__auto__.push((arguments[i__18451__auto___6895]));

var G__6896 = (i__18451__auto___6895 + (1));
i__18451__auto___6895 = G__6896;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.cond__GT_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.cond__GT_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,expr,clauses){
if(cljs.core.even_QMARK_(cljs.core.count(clauses))){
} else {
throw (new Error("Assert failed: (even? (count clauses))"));
}

var g = cljs.core.gensym.cljs$core$IFn$_invoke$arity$0();
var steps = cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (g){
return (function (p__6889){
var vec__6890 = p__6889;
var test = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__6890,(0),null);
var step = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__6890,(1),null);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = test;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","->","cljs.core$macros/->",-1519455206,null)),(function (){var x__18265__auto__ = g;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = step;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = g;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});})(g))
,cljs.core.partition.cljs$core$IFn$_invoke$arity$2((2),clauses));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = g;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.interleave.cljs$core$IFn$_invoke$arity$2(cljs.core.repeat.cljs$core$IFn$_invoke$arity$1(g),cljs.core.butlast(steps))], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = ((cljs.core.empty_QMARK_(steps))?g:cljs.core.last(steps));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.cond__GT_.cljs$lang$maxFixedArity = (3);

cljs.core$macros.cond__GT_.cljs$lang$applyTo = (function (seq6885){
var G__6886 = cljs.core.first(seq6885);
var seq6885__$1 = cljs.core.next(seq6885);
var G__6887 = cljs.core.first(seq6885__$1);
var seq6885__$2 = cljs.core.next(seq6885__$1);
var G__6888 = cljs.core.first(seq6885__$2);
var seq6885__$3 = cljs.core.next(seq6885__$2);
return cljs.core$macros.cond__GT_.cljs$core$IFn$_invoke$arity$variadic(G__6886,G__6887,G__6888,seq6885__$3);
});

return null;
})()
;
cljs.core$macros.cond__GT_.cljs$lang$macro = true;

var ret__18490__auto___6905 = (function (){
/**
 * Takes an expression and a set of test/form pairs. Threads expr (via ->>)
 *   through each form for which the corresponding test expression
 *   is true.  Note that, unlike cond branching, cond->> threading does not short circuit
 *   after the first true test expression.
 */
cljs.core$macros.cond__GT__GT_ = (function cljs$core$macros$cond__GT__GT_(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6906 = arguments.length;
var i__18451__auto___6907 = (0);
while(true){
if((i__18451__auto___6907 < len__18450__auto___6906)){
args__18453__auto__.push((arguments[i__18451__auto___6907]));

var G__6908 = (i__18451__auto___6907 + (1));
i__18451__auto___6907 = G__6908;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.cond__GT__GT_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.cond__GT__GT_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,expr,clauses){
if(cljs.core.even_QMARK_(cljs.core.count(clauses))){
} else {
throw (new Error("Assert failed: (even? (count clauses))"));
}

var g = cljs.core.gensym.cljs$core$IFn$_invoke$arity$0();
var steps = cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (g){
return (function (p__6901){
var vec__6902 = p__6901;
var test = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__6902,(0),null);
var step = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__6902,(1),null);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = test;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","->>","cljs.core$macros/->>",-1353108561,null)),(function (){var x__18265__auto__ = g;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = step;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = g;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});})(g))
,cljs.core.partition.cljs$core$IFn$_invoke$arity$2((2),clauses));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = g;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.interleave.cljs$core$IFn$_invoke$arity$2(cljs.core.repeat.cljs$core$IFn$_invoke$arity$1(g),cljs.core.butlast(steps))], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = ((cljs.core.empty_QMARK_(steps))?g:cljs.core.last(steps));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.cond__GT__GT_.cljs$lang$maxFixedArity = (3);

cljs.core$macros.cond__GT__GT_.cljs$lang$applyTo = (function (seq6897){
var G__6898 = cljs.core.first(seq6897);
var seq6897__$1 = cljs.core.next(seq6897);
var G__6899 = cljs.core.first(seq6897__$1);
var seq6897__$2 = cljs.core.next(seq6897__$1);
var G__6900 = cljs.core.first(seq6897__$2);
var seq6897__$3 = cljs.core.next(seq6897__$2);
return cljs.core$macros.cond__GT__GT_.cljs$core$IFn$_invoke$arity$variadic(G__6898,G__6899,G__6900,seq6897__$3);
});

return null;
})()
;
cljs.core$macros.cond__GT__GT_.cljs$lang$macro = true;

var ret__18490__auto___6914 = (function (){
/**
 * Binds name to expr, evaluates the first form in the lexical context
 *   of that binding, then binds name to that result, repeating for each
 *   successive form, returning the result of the last form.
 */
cljs.core$macros.as__GT_ = (function cljs$core$macros$as__GT_(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6915 = arguments.length;
var i__18451__auto___6916 = (0);
while(true){
if((i__18451__auto___6916 < len__18450__auto___6915)){
args__18453__auto__.push((arguments[i__18451__auto___6916]));

var G__6917 = (i__18451__auto___6916 + (1));
i__18451__auto___6916 = G__6917;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((4) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((4)),(0),null)):null);
return cljs.core$macros.as__GT_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18454__auto__);
});

cljs.core$macros.as__GT_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,expr,name,forms){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = name;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.interleave.cljs$core$IFn$_invoke$arity$2(cljs.core.repeat.cljs$core$IFn$_invoke$arity$1(name),cljs.core.butlast(forms))], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = ((cljs.core.empty_QMARK_(forms))?name:cljs.core.last(forms));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.as__GT_.cljs$lang$maxFixedArity = (4);

cljs.core$macros.as__GT_.cljs$lang$applyTo = (function (seq6909){
var G__6910 = cljs.core.first(seq6909);
var seq6909__$1 = cljs.core.next(seq6909);
var G__6911 = cljs.core.first(seq6909__$1);
var seq6909__$2 = cljs.core.next(seq6909__$1);
var G__6912 = cljs.core.first(seq6909__$2);
var seq6909__$3 = cljs.core.next(seq6909__$2);
var G__6913 = cljs.core.first(seq6909__$3);
var seq6909__$4 = cljs.core.next(seq6909__$3);
return cljs.core$macros.as__GT_.cljs$core$IFn$_invoke$arity$variadic(G__6910,G__6911,G__6912,G__6913,seq6909__$4);
});

return null;
})()
;
cljs.core$macros.as__GT_.cljs$lang$macro = true;

var ret__18490__auto___6922 = (function (){
/**
 * When expr is not nil, threads it into the first form (via ->),
 *   and when that result is not nil, through the next etc
 */
cljs.core$macros.some__GT_ = (function cljs$core$macros$some__GT_(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6923 = arguments.length;
var i__18451__auto___6924 = (0);
while(true){
if((i__18451__auto___6924 < len__18450__auto___6923)){
args__18453__auto__.push((arguments[i__18451__auto___6924]));

var G__6925 = (i__18451__auto___6924 + (1));
i__18451__auto___6924 = G__6925;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.some__GT_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.some__GT_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,expr,forms){
var g = cljs.core.gensym.cljs$core$IFn$_invoke$arity$0();
var steps = cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (g){
return (function (step){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),(function (){var x__18265__auto__ = g;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,null),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","->","cljs.core$macros/->",-1519455206,null)),(function (){var x__18265__auto__ = g;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = step;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});})(g))
,forms);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = g;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.interleave.cljs$core$IFn$_invoke$arity$2(cljs.core.repeat.cljs$core$IFn$_invoke$arity$1(g),cljs.core.butlast(steps))], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = ((cljs.core.empty_QMARK_(steps))?g:cljs.core.last(steps));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.some__GT_.cljs$lang$maxFixedArity = (3);

cljs.core$macros.some__GT_.cljs$lang$applyTo = (function (seq6918){
var G__6919 = cljs.core.first(seq6918);
var seq6918__$1 = cljs.core.next(seq6918);
var G__6920 = cljs.core.first(seq6918__$1);
var seq6918__$2 = cljs.core.next(seq6918__$1);
var G__6921 = cljs.core.first(seq6918__$2);
var seq6918__$3 = cljs.core.next(seq6918__$2);
return cljs.core$macros.some__GT_.cljs$core$IFn$_invoke$arity$variadic(G__6919,G__6920,G__6921,seq6918__$3);
});

return null;
})()
;
cljs.core$macros.some__GT_.cljs$lang$macro = true;

var ret__18490__auto___6930 = (function (){
/**
 * When expr is not nil, threads it into the first form (via ->>),
 *   and when that result is not nil, through the next etc
 */
cljs.core$macros.some__GT__GT_ = (function cljs$core$macros$some__GT__GT_(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6931 = arguments.length;
var i__18451__auto___6932 = (0);
while(true){
if((i__18451__auto___6932 < len__18450__auto___6931)){
args__18453__auto__.push((arguments[i__18451__auto___6932]));

var G__6933 = (i__18451__auto___6932 + (1));
i__18451__auto___6932 = G__6933;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.some__GT__GT_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.some__GT__GT_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,expr,forms){
var g = cljs.core.gensym.cljs$core$IFn$_invoke$arity$0();
var steps = cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (g){
return (function (step){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),(function (){var x__18265__auto__ = g;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,null),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","->>","cljs.core$macros/->>",-1353108561,null)),(function (){var x__18265__auto__ = g;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = step;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});})(g))
,forms);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = g;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.interleave.cljs$core$IFn$_invoke$arity$2(cljs.core.repeat.cljs$core$IFn$_invoke$arity$1(g),cljs.core.butlast(steps))], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = ((cljs.core.empty_QMARK_(steps))?g:cljs.core.last(steps));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.some__GT__GT_.cljs$lang$maxFixedArity = (3);

cljs.core$macros.some__GT__GT_.cljs$lang$applyTo = (function (seq6926){
var G__6927 = cljs.core.first(seq6926);
var seq6926__$1 = cljs.core.next(seq6926);
var G__6928 = cljs.core.first(seq6926__$1);
var seq6926__$2 = cljs.core.next(seq6926__$1);
var G__6929 = cljs.core.first(seq6926__$2);
var seq6926__$3 = cljs.core.next(seq6926__$2);
return cljs.core$macros.some__GT__GT_.cljs$core$IFn$_invoke$arity$variadic(G__6927,G__6928,G__6929,seq6926__$3);
});

return null;
})()
;
cljs.core$macros.some__GT__GT_.cljs$lang$macro = true;

var ret__18490__auto___6943 = (function (){
/**
 * bindings => binding-form test
 * 
 *    If test is not nil, evaluates then with binding-form bound to the
 *    value of test, if not, yields else
 */
cljs.core$macros.if_some = (function cljs$core$macros$if_some(var_args){
var G__6942 = arguments.length;
switch (G__6942) {
case 4:
return cljs.core$macros.if_some.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___6945 = arguments.length;
var i__18451__auto___6946 = (0);
while(true){
if((i__18451__auto___6946 < len__18450__auto___6945)){
args_arr__18469__auto__.push((arguments[i__18451__auto___6946]));

var G__6947 = (i__18451__auto___6946 + (1));
i__18451__auto___6946 = G__6947;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((5)),(0),null));
return cljs.core$macros.if_some.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),argseq__18470__auto__);

}
});

cljs.core$macros.if_some.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,then){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","if-some","cljs.core$macros/if-some",1405341529,null)),(function (){var x__18265__auto__ = bindings;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = then;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
});

cljs.core$macros.if_some.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,then,else$,oldform){
if(cljs.core.vector_QMARK_(bindings)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("if-some requires a vector for its binding",cljs.core.PersistentArrayMap.EMPTY);
}

if(cljs.core.empty_QMARK_(oldform)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("if-some requires 1 or 2 forms after binding vector",cljs.core.PersistentArrayMap.EMPTY);
}

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((2),cljs.core.count(bindings))){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("if-some requires exactly 2 forms in binding vector",cljs.core.PersistentArrayMap.EMPTY);
}


var form = (bindings.cljs$core$IFn$_invoke$arity$1 ? bindings.cljs$core$IFn$_invoke$arity$1((0)) : bindings.call(null,(0)));
var tst = (bindings.cljs$core$IFn$_invoke$arity$1 ? bindings.cljs$core$IFn$_invoke$arity$1((1)) : bindings.call(null,(1)));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"temp__6934__auto__","temp__6934__auto__",1879726140,null)),(function (){var x__18265__auto__ = tst;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"temp__6934__auto__","temp__6934__auto__",1879726140,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = else$;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = form;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"temp__6934__auto__","temp__6934__auto__",1879726140,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = then;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.if_some.cljs$lang$applyTo = (function (seq6936){
var G__6937 = cljs.core.first(seq6936);
var seq6936__$1 = cljs.core.next(seq6936);
var G__6938 = cljs.core.first(seq6936__$1);
var seq6936__$2 = cljs.core.next(seq6936__$1);
var G__6939 = cljs.core.first(seq6936__$2);
var seq6936__$3 = cljs.core.next(seq6936__$2);
var G__6940 = cljs.core.first(seq6936__$3);
var seq6936__$4 = cljs.core.next(seq6936__$3);
var G__6941 = cljs.core.first(seq6936__$4);
var seq6936__$5 = cljs.core.next(seq6936__$4);
return cljs.core$macros.if_some.cljs$core$IFn$_invoke$arity$variadic(G__6937,G__6938,G__6939,G__6940,G__6941,seq6936__$5);
});

cljs.core$macros.if_some.cljs$lang$maxFixedArity = (5);

return null;
})()
;
cljs.core$macros.if_some.cljs$lang$macro = true;

var ret__18490__auto___6953 = (function (){
/**
 * bindings => binding-form test
 * 
 *    When test is not nil, evaluates body with binding-form bound to the
 *    value of test
 */
cljs.core$macros.when_some = (function cljs$core$macros$when_some(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6954 = arguments.length;
var i__18451__auto___6955 = (0);
while(true){
if((i__18451__auto___6955 < len__18450__auto___6954)){
args__18453__auto__.push((arguments[i__18451__auto___6955]));

var G__6956 = (i__18451__auto___6955 + (1));
i__18451__auto___6955 = G__6956;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.when_some.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.when_some.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,body){
if(cljs.core.vector_QMARK_(bindings)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("when-some requires a vector for its binding",cljs.core.PersistentArrayMap.EMPTY);
}

if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((2),cljs.core.count(bindings))){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("when-some requires exactly 2 forms in binding vector",cljs.core.PersistentArrayMap.EMPTY);
}


var form = (bindings.cljs$core$IFn$_invoke$arity$1 ? bindings.cljs$core$IFn$_invoke$arity$1((0)) : bindings.call(null,(0)));
var tst = (bindings.cljs$core$IFn$_invoke$arity$1 ? bindings.cljs$core$IFn$_invoke$arity$1((1)) : bindings.call(null,(1)));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"temp__6948__auto__","temp__6948__auto__",724987268,null)),(function (){var x__18265__auto__ = tst;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"temp__6948__auto__","temp__6948__auto__",724987268,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,null),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = form;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"temp__6948__auto__","temp__6948__auto__",724987268,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.when_some.cljs$lang$maxFixedArity = (3);

cljs.core$macros.when_some.cljs$lang$applyTo = (function (seq6949){
var G__6950 = cljs.core.first(seq6949);
var seq6949__$1 = cljs.core.next(seq6949);
var G__6951 = cljs.core.first(seq6949__$1);
var seq6949__$2 = cljs.core.next(seq6949__$1);
var G__6952 = cljs.core.first(seq6949__$2);
var seq6949__$3 = cljs.core.next(seq6949__$2);
return cljs.core$macros.when_some.cljs$core$IFn$_invoke$arity$variadic(G__6950,G__6951,G__6952,seq6949__$3);
});

return null;
})()
;
cljs.core$macros.when_some.cljs$lang$macro = true;

/**
 * A good fdecl looks like (([a] ...) ([a b] ...)) near the end of defn.
 */
cljs.core$macros.assert_valid_fdecl = (function cljs$core$macros$assert_valid_fdecl(fdecl){
if(cljs.core.empty_QMARK_(fdecl)){
throw (new Error("Parameter declaration missing"));
} else {
}

var argdecls = cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__6957_SHARP_){
if(cljs.core.seq_QMARK_(p1__6957_SHARP_)){
return cljs.core.first(p1__6957_SHARP_);
} else {
throw (new Error(((cljs.core.seq_QMARK_(cljs.core.first(fdecl)))?["Invalid signature \"",cljs.core.str.cljs$core$IFn$_invoke$arity$1(p1__6957_SHARP_),"\" should be a list"].join(''):["Parameter declaration \"",cljs.core.str.cljs$core$IFn$_invoke$arity$1(p1__6957_SHARP_),"\" should be a vector"].join(''))));
}
}),fdecl);
var bad_args = cljs.core.seq(cljs.core.remove.cljs$core$IFn$_invoke$arity$2(((function (argdecls){
return (function (p1__6958_SHARP_){
return cljs.core.vector_QMARK_(p1__6958_SHARP_);
});})(argdecls))
,argdecls));
if(bad_args){
throw (new Error(["Parameter declaration \"",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.first(bad_args)),"\" should be a vector"].join('')));
} else {
return null;
}
});
cljs.core$macros.sigs = (function cljs$core$macros$sigs(fdecl){
(cljs.core$macros.assert_valid_fdecl.cljs$core$IFn$_invoke$arity$1 ? cljs.core$macros.assert_valid_fdecl.cljs$core$IFn$_invoke$arity$1(fdecl) : cljs.core$macros.assert_valid_fdecl.call(null,fdecl));

var asig = (function (fdecl__$1){
var arglist = cljs.core.first(fdecl__$1);
var arglist__$1 = ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Symbol(null,"&form","&form",1482799337,null),cljs.core.first(arglist)))?cljs.core.subvec.cljs$core$IFn$_invoke$arity$3(arglist,(2),cljs.core.count(arglist)):arglist);
var body = cljs.core.next(fdecl__$1);
if(cljs.core.map_QMARK_(cljs.core.first(body))){
if(cljs.core.next(body)){
return cljs.core.with_meta(arglist__$1,cljs.core.conj.cljs$core$IFn$_invoke$arity$2((cljs.core.truth_(cljs.core.meta(arglist__$1))?cljs.core.meta(arglist__$1):cljs.core.PersistentArrayMap.EMPTY),cljs.core.first(body)));
} else {
return arglist__$1;
}
} else {
return arglist__$1;
}
});
if(cljs.core.seq_QMARK_(cljs.core.first(fdecl))){
var ret = cljs.core.PersistentVector.EMPTY;
var fdecls = fdecl;
while(true){
if(cljs.core.truth_(fdecls)){
var G__6959 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(ret,asig(cljs.core.first(fdecls)));
var G__6960 = cljs.core.next(fdecls);
ret = G__6959;
fdecls = G__6960;
continue;
} else {
return cljs.core.seq(ret);
}
break;
}
} else {
var x__18265__auto__ = asig(fdecl);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
}
});
var ret__18490__auto___6961 = /**
 * defs name to have the root value of init iff the named var has no root value,
 *   else init is unevaluated
 */
cljs.core$macros.defonce = (function cljs$core$macros$defonce(_AMPERSAND_form,_AMPERSAND_env,x,init){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when-not","cljs.core$macros/when-not",-764302244,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","exists?","cljs.core$macros/exists?",-1828590389,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"def","def",597100991,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = init;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.defonce.cljs$lang$macro = true;

cljs.core$macros.destructure = (function cljs$core$macros$destructure(bindings){
var bents = cljs.core.partition.cljs$core$IFn$_invoke$arity$2((2),bindings);
var pb = ((function (bents){
return (function cljs$core$macros$destructure_$_pb(bvec,b,v){
var pvec = ((function (bents){
return (function (bvec__$1,b__$1,val){
var gvec = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("vec__");
var gseq = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("seq__");
var gfirst = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("first__");
var has_rest = cljs.core.some(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Symbol(null,"&","&",-2144855648,null),null], null), null),b__$1);
var ret = (function (){var ret = cljs.core.conj.cljs$core$IFn$_invoke$arity$variadic(bvec__$1,gvec,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([val], 0));
if(cljs.core.truth_(has_rest)){
return cljs.core.conj.cljs$core$IFn$_invoke$arity$variadic(ret,gseq,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj((function (){var x__18265__auto__ = gvec;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),new cljs.core.Symbol("cljs.core$macros","seq","cljs.core$macros/seq",-1873195024,null))], 0));
} else {
return ret;
}
})();
var n = (0);
var bs = b__$1;
var seen_rest_QMARK_ = false;
while(true){
if(cljs.core.seq(bs)){
var firstb = cljs.core.first(bs);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(firstb,new cljs.core.Symbol(null,"&","&",-2144855648,null))){
var G__6972 = cljs$core$macros$destructure_$_pb(ret,cljs.core.second(bs),gseq);
var G__6973 = n;
var G__6974 = cljs.core.nnext(bs);
var G__6975 = true;
ret = G__6972;
n = G__6973;
bs = G__6974;
seen_rest_QMARK_ = G__6975;
continue;
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(firstb,new cljs.core.Keyword(null,"as","as",1148689641))){
return cljs$core$macros$destructure_$_pb(ret,cljs.core.second(bs),gvec);
} else {
if(seen_rest_QMARK_){
throw (new Error("Unsupported binding form, only :as can follow & parameter"));
} else {
var G__6976 = cljs$core$macros$destructure_$_pb((cljs.core.truth_(has_rest)?cljs.core.conj.cljs$core$IFn$_invoke$arity$variadic(ret,gfirst,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","first","cljs.core$macros/first",-999127023,null)),(function (){var x__18265__auto__ = gseq;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))),gseq,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","next","cljs.core$macros/next",-1478604758,null)),(function (){var x__18265__auto__ = gseq;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())))], 0)):ret),firstb,(cljs.core.truth_(has_rest)?gfirst:cljs.core._conj((function (){var x__18265__auto__ = gvec;
return cljs.core._conj((function (){var x__18265__auto____$1 = n;
return cljs.core._conj(cljs.core._conj(cljs.core.List.EMPTY,null),x__18265__auto____$1);
})(),x__18265__auto__);
})(),new cljs.core.Symbol("cljs.core$macros","nth","cljs.core$macros/nth",2017140738,null))));
var G__6977 = (n + (1));
var G__6978 = cljs.core.next(bs);
var G__6979 = seen_rest_QMARK_;
ret = G__6976;
n = G__6977;
bs = G__6978;
seen_rest_QMARK_ = G__6979;
continue;
}

}
}
} else {
return ret;
}
break;
}
});})(bents))
;
var pmap = ((function (pvec,bents){
return (function (bvec__$1,b__$1,v__$1){
var gmap = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("map__");
var defaults = new cljs.core.Keyword(null,"or","or",235744169).cljs$core$IFn$_invoke$arity$1(b__$1);
var ret = (function (){var G__6968 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(cljs.core.conj.cljs$core$IFn$_invoke$arity$2(cljs.core.conj.cljs$core$IFn$_invoke$arity$2(cljs.core.conj.cljs$core$IFn$_invoke$arity$2(bvec__$1,gmap),v__$1),gmap),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","implements?","cljs.core$macros/implements?",-94762250,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","ISeq","cljs.core$macros/ISeq",962557029,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = gmap;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","apply","cljs.core$macros/apply",1565934668,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","hash-map","cljs.core/hash-map",303385767,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = gmap;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = gmap;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
var fexpr__6967 = ((function (G__6968,gmap,defaults,pvec,bents){
return (function (ret){
if(cljs.core.truth_(new cljs.core.Keyword(null,"as","as",1148689641).cljs$core$IFn$_invoke$arity$1(b__$1))){
return cljs.core.conj.cljs$core$IFn$_invoke$arity$variadic(ret,new cljs.core.Keyword(null,"as","as",1148689641).cljs$core$IFn$_invoke$arity$1(b__$1),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([gmap], 0));
} else {
return ret;
}
});})(G__6968,gmap,defaults,pvec,bents))
;
return fexpr__6967(G__6968);
})();
var bes = (function (){var transforms = cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(((function (ret,gmap,defaults,pvec,bents){
return (function (transforms,mk){
if((mk instanceof cljs.core.Keyword)){
var mkns = cljs.core.namespace(mk);
var mkn = cljs.core.name(mk);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(mkn,"keys")){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(transforms,mk,((function (mkns,mkn,ret,gmap,defaults,pvec,bents){
return (function (p1__6962_SHARP_){
return cljs.core.keyword.cljs$core$IFn$_invoke$arity$2((function (){var or__17767__auto__ = mkns;
if(cljs.core.truth_(or__17767__auto__)){
return or__17767__auto__;
} else {
return cljs.core.namespace(p1__6962_SHARP_);
}
})(),cljs.core.name(p1__6962_SHARP_));
});})(mkns,mkn,ret,gmap,defaults,pvec,bents))
);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(mkn,"syms")){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(transforms,mk,((function (mkns,mkn,ret,gmap,defaults,pvec,bents){
return (function (p1__6963_SHARP_){
return cljs.core._conj((function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$2((function (){var or__17767__auto__ = mkns;
if(cljs.core.truth_(or__17767__auto__)){
return or__17767__auto__;
} else {
return cljs.core.namespace(p1__6963_SHARP_);
}
})(),cljs.core.name(p1__6963_SHARP_));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),new cljs.core.Symbol(null,"quote","quote",1377916282,null));
});})(mkns,mkn,ret,gmap,defaults,pvec,bents))
);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(mkn,"strs")){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(transforms,mk,cljs.core.str);
} else {
return transforms;

}
}
}
} else {
return transforms;
}
});})(ret,gmap,defaults,pvec,bents))
,cljs.core.PersistentArrayMap.EMPTY,cljs.core.keys(b__$1));
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(((function (transforms,ret,gmap,defaults,pvec,bents){
return (function (bes,entry){
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(((function (transforms,ret,gmap,defaults,pvec,bents){
return (function (p1__6964_SHARP_,p2__6965_SHARP_){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(p1__6964_SHARP_,p2__6965_SHARP_,(function (){var fexpr__6969 = cljs.core.val(entry);
return (fexpr__6969.cljs$core$IFn$_invoke$arity$1 ? fexpr__6969.cljs$core$IFn$_invoke$arity$1(p2__6965_SHARP_) : fexpr__6969.call(null,p2__6965_SHARP_));
})());
});})(transforms,ret,gmap,defaults,pvec,bents))
,cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(bes,cljs.core.key(entry)),(function (){var fexpr__6970 = cljs.core.key(entry);
return (fexpr__6970.cljs$core$IFn$_invoke$arity$1 ? fexpr__6970.cljs$core$IFn$_invoke$arity$1(bes) : fexpr__6970.call(null,bes));
})());
});})(transforms,ret,gmap,defaults,pvec,bents))
,cljs.core.dissoc.cljs$core$IFn$_invoke$arity$variadic(b__$1,new cljs.core.Keyword(null,"as","as",1148689641),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"or","or",235744169)], 0)),transforms);
})();
while(true){
if(cljs.core.seq(bes)){
var bb = cljs.core.key(cljs.core.first(bes));
var bk = cljs.core.val(cljs.core.first(bes));
var local = ((((!((bb == null)))?((((bb.cljs$lang$protocol_mask$partition1$ & (4096))) || ((cljs.core.PROTOCOL_SENTINEL === bb.cljs$core$INamed$)))?true:false):false))?cljs.core.with_meta(cljs.core.symbol.cljs$core$IFn$_invoke$arity$2(null,cljs.core.name(bb)),cljs.core.meta(bb)):bb);
var bv = ((cljs.core.contains_QMARK_(defaults,local))?cljs.core._conj((function (){var x__18265__auto__ = gmap;
return cljs.core._conj((function (){var x__18265__auto____$1 = bk;
return cljs.core._conj((function (){var x__18265__auto____$2 = (defaults.cljs$core$IFn$_invoke$arity$1 ? defaults.cljs$core$IFn$_invoke$arity$1(local) : defaults.call(null,local));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$2);
})(),x__18265__auto____$1);
})(),x__18265__auto__);
})(),new cljs.core.Symbol("cljs.core","get","cljs.core/get",-296075407,null)):cljs.core._conj((function (){var x__18265__auto__ = gmap;
return cljs.core._conj((function (){var x__18265__auto____$1 = bk;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),new cljs.core.Symbol("cljs.core","get","cljs.core/get",-296075407,null)));
var G__6980 = ((((bb instanceof cljs.core.Keyword)) || ((bb instanceof cljs.core.Symbol)))?cljs.core.conj.cljs$core$IFn$_invoke$arity$variadic(ret,local,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([bv], 0)):cljs$core$macros$destructure_$_pb(ret,bb,bv));
var G__6981 = cljs.core.next(bes);
ret = G__6980;
bes = G__6981;
continue;
} else {
return ret;
}
break;
}
});})(pvec,bents))
;
if((b instanceof cljs.core.Symbol)){
return cljs.core.conj.cljs$core$IFn$_invoke$arity$2(cljs.core.conj.cljs$core$IFn$_invoke$arity$2(bvec,(cljs.core.truth_(cljs.core.namespace(b))?cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(cljs.core.name(b)):b)),v);
} else {
if((b instanceof cljs.core.Keyword)){
return cljs.core.conj.cljs$core$IFn$_invoke$arity$2(cljs.core.conj.cljs$core$IFn$_invoke$arity$2(bvec,cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(cljs.core.name(b))),v);
} else {
if(cljs.core.vector_QMARK_(b)){
return pvec(bvec,b,v);
} else {
if(cljs.core.map_QMARK_(b)){
return pmap(bvec,b,v);
} else {
throw (new Error(["Unsupported binding form: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(b)].join('')));

}
}
}
}
});})(bents))
;
var process_entry = ((function (bents,pb){
return (function (bvec,b){
return pb(bvec,cljs.core.first(b),cljs.core.second(b));
});})(bents,pb))
;
if(cljs.core.every_QMARK_(cljs.core.symbol_QMARK_,cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.first,bents))){
return bindings;
} else {
var temp__5457__auto__ = cljs.core.seq(cljs.core.filter.cljs$core$IFn$_invoke$arity$2(((function (bents,pb,process_entry){
return (function (p1__6966_SHARP_){
return (cljs.core.first(p1__6966_SHARP_) instanceof cljs.core.Keyword);
});})(bents,pb,process_entry))
,bents));
if(temp__5457__auto__){
var kwbs = temp__5457__auto__;
throw (new Error(["Unsupported binding key: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.ffirst(kwbs))].join('')));
} else {
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(process_entry,cljs.core.PersistentVector.EMPTY,bents);
}
}
});
var ret__18490__auto___6982 = /**
 * Defines a var using `goog.define`. Passed default value must be
 *   string, number or boolean.
 * 
 *   Default value can be overridden at compile time using the
 *   compiler option `:closure-defines`.
 * 
 *   Example:
 *  (ns your-app.core)
 *  (goog-define DEBUG! false)
 *  ;; can be overridden with
 *  :closure-defines {"your_app.core.DEBUG_BANG_" true}
 *  or
 *  :closure-defines {'your-app.core/DEBUG! true}
 */
cljs.core$macros.goog_define = (function cljs$core$macros$goog_define(_AMPERSAND_form,_AMPERSAND_env,sym,default$){
if((typeof default$ === 'string') || (typeof default$ === 'number') || (default$ === true) || (default$ === false)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("goog-define requires a string, number or boolean as default value",cljs.core.PersistentArrayMap.EMPTY);
}


var defname = cljs.compiler.munge.cljs$core$IFn$_invoke$arity$1([cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core._STAR_ns_STAR_),"/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(sym)].join(''));
var type = ((typeof default$ === 'string')?"string":((typeof default$ === 'number')?"number":(((default$ === true) || (default$ === false))?"boolean":null)));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","declare","cljs.core$macros/declare",1172642527,null)),(function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(sym);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"js*","js*",-1134233646,null)),(function (){var x__18265__auto__ = ["/** @define {",cljs.core.str.cljs$core$IFn$_invoke$arity$1(type),"} */"].join('');
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("goog","define","goog/define",-352722538,null)),(function (){var x__18265__auto__ = defname;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = default$;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.goog_define.cljs$lang$macro = true;

var ret__18490__auto___6987 = (function (){
/**
 * binding => binding-form init-expr
 * 
 *   Evaluates the exprs in a lexical context in which the symbols in
 *   the binding-forms are bound to their respective init-exprs or parts
 *   therein.
 */
cljs.core$macros.let$ = (function cljs$core$macros$let(var_args){
var args__18453__auto__ = [];
var len__18450__auto___6988 = arguments.length;
var i__18451__auto___6989 = (0);
while(true){
if((i__18451__auto___6989 < len__18450__auto___6988)){
args__18453__auto__.push((arguments[i__18451__auto___6989]));

var G__6990 = (i__18451__auto___6989 + (1));
i__18451__auto___6989 = G__6990;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.let$.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.let$.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,body){
if(cljs.core.vector_QMARK_(bindings)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("let requires a vector for its binding",cljs.core.PersistentArrayMap.EMPTY);
}

if(cljs.core.even_QMARK_(cljs.core.count(bindings))){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("let requires an even number of forms in binding vector",cljs.core.PersistentArrayMap.EMPTY);
}


return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"let*","let*",1920721458,null)),(function (){var x__18265__auto__ = cljs.core$macros.destructure(bindings);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
});

cljs.core$macros.let$.cljs$lang$maxFixedArity = (3);

cljs.core$macros.let$.cljs$lang$applyTo = (function (seq6983){
var G__6984 = cljs.core.first(seq6983);
var seq6983__$1 = cljs.core.next(seq6983);
var G__6985 = cljs.core.first(seq6983__$1);
var seq6983__$2 = cljs.core.next(seq6983__$1);
var G__6986 = cljs.core.first(seq6983__$2);
var seq6983__$3 = cljs.core.next(seq6983__$2);
return cljs.core$macros.let$.cljs$core$IFn$_invoke$arity$variadic(G__6984,G__6985,G__6986,seq6983__$3);
});

return null;
})()
;
cljs.core$macros.let$.cljs$lang$macro = true;

var ret__18490__auto___6999 = (function (){
/**
 * Evaluates the exprs in a lexical context in which the symbols in
 *   the binding-forms are bound to their respective init-exprs or parts
 *   therein. Acts as a recur target.
 */
cljs.core$macros.loop = (function cljs$core$macros$loop(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7000 = arguments.length;
var i__18451__auto___7001 = (0);
while(true){
if((i__18451__auto___7001 < len__18450__auto___7000)){
args__18453__auto__.push((arguments[i__18451__auto___7001]));

var G__7002 = (i__18451__auto___7001 + (1));
i__18451__auto___7001 = G__7002;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.loop.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.loop.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,body){
if(cljs.core.vector_QMARK_(bindings)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("loop requires a vector for its binding",cljs.core.PersistentArrayMap.EMPTY);
}

if(cljs.core.even_QMARK_(cljs.core.count(bindings))){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("loop requires an even number of forms in binding vector",cljs.core.PersistentArrayMap.EMPTY);
}


var db = cljs.core$macros.destructure(bindings);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(db,bindings)){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"loop*","loop*",615029416,null)),(function (){var x__18265__auto__ = bindings;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
} else {
var vs = cljs.core.take_nth.cljs$core$IFn$_invoke$arity$2((2),cljs.core.drop.cljs$core$IFn$_invoke$arity$2((1),bindings));
var bs = cljs.core.take_nth.cljs$core$IFn$_invoke$arity$2((2),bindings);
var gs = cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (vs,bs,db){
return (function (b){
if((b instanceof cljs.core.Symbol)){
return b;
} else {
return cljs.core.gensym.cljs$core$IFn$_invoke$arity$0();
}
});})(vs,bs,db))
,bs);
var bfs = cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(((function (vs,bs,gs,db){
return (function (ret,p__6995){
var vec__6996 = p__6995;
var b = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__6996,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__6996,(1),null);
var g = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__6996,(2),null);
if((b instanceof cljs.core.Symbol)){
return cljs.core.conj.cljs$core$IFn$_invoke$arity$variadic(ret,g,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([v], 0));
} else {
return cljs.core.conj.cljs$core$IFn$_invoke$arity$variadic(ret,g,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([v,b,g], 0));
}
});})(vs,bs,gs,db))
,cljs.core.PersistentVector.EMPTY,cljs.core.map.cljs$core$IFn$_invoke$arity$4(cljs.core.vector,bs,vs,gs));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = bfs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"loop*","loop*",615029416,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.interleave.cljs$core$IFn$_invoke$arity$2(gs,gs));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.interleave.cljs$core$IFn$_invoke$arity$2(bs,gs));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
}
});

cljs.core$macros.loop.cljs$lang$maxFixedArity = (3);

cljs.core$macros.loop.cljs$lang$applyTo = (function (seq6991){
var G__6992 = cljs.core.first(seq6991);
var seq6991__$1 = cljs.core.next(seq6991);
var G__6993 = cljs.core.first(seq6991__$1);
var seq6991__$2 = cljs.core.next(seq6991__$1);
var G__6994 = cljs.core.first(seq6991__$2);
var seq6991__$3 = cljs.core.next(seq6991__$2);
return cljs.core$macros.loop.cljs$core$IFn$_invoke$arity$variadic(G__6992,G__6993,G__6994,seq6991__$3);
});

return null;
})()
;
cljs.core$macros.loop.cljs$lang$macro = true;

/**
 * protocol fqn -> [partition number, bit]
 */
cljs.core$macros.fast_path_protocols = cljs.core.zipmap(cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__7003_SHARP_){
return cljs.core.symbol.cljs$core$IFn$_invoke$arity$2("cljs.core",[cljs.core.str.cljs$core$IFn$_invoke$arity$1(p1__7003_SHARP_)].join(''));
}),cljs.core.PersistentVector.fromArray([new cljs.core.Symbol(null,"IFn","IFn",-244881638,null),new cljs.core.Symbol(null,"ICounted","ICounted",-1705786327,null),new cljs.core.Symbol(null,"IEmptyableCollection","IEmptyableCollection",1477271438,null),new cljs.core.Symbol(null,"ICollection","ICollection",-686709190,null),new cljs.core.Symbol(null,"IIndexed","IIndexed",-574812826,null),new cljs.core.Symbol(null,"ASeq","ASeq",266390234,null),new cljs.core.Symbol(null,"ISeq","ISeq",1517365813,null),new cljs.core.Symbol(null,"INext","INext",562211849,null),new cljs.core.Symbol(null,"ILookup","ILookup",784647298,null),new cljs.core.Symbol(null,"IAssociative","IAssociative",-1306431882,null),new cljs.core.Symbol(null,"IMap","IMap",992876629,null),new cljs.core.Symbol(null,"IMapEntry","IMapEntry",-943754199,null),new cljs.core.Symbol(null,"ISet","ISet",-1398072657,null),new cljs.core.Symbol(null,"IStack","IStack",1136769449,null),new cljs.core.Symbol(null,"IVector","IVector",-1120721434,null),new cljs.core.Symbol(null,"IDeref","IDeref",1738423197,null),new cljs.core.Symbol(null,"IDerefWithTimeout","IDerefWithTimeout",2140974319,null),new cljs.core.Symbol(null,"IMeta","IMeta",1095313672,null),new cljs.core.Symbol(null,"IWithMeta","IWithMeta",-509493158,null),new cljs.core.Symbol(null,"IReduce","IReduce",-440384974,null),new cljs.core.Symbol(null,"IKVReduce","IKVReduce",-870856862,null),new cljs.core.Symbol(null,"IEquiv","IEquiv",-1912850869,null),new cljs.core.Symbol(null,"IHash","IHash",-1495374645,null),new cljs.core.Symbol(null,"ISeqable","ISeqable",1349682102,null),new cljs.core.Symbol(null,"ISequential","ISequential",-1626174217,null),new cljs.core.Symbol(null,"IList","IList",1682281311,null),new cljs.core.Symbol(null,"IRecord","IRecord",-903221169,null),new cljs.core.Symbol(null,"IReversible","IReversible",-723048599,null),new cljs.core.Symbol(null,"ISorted","ISorted",-253627362,null),new cljs.core.Symbol(null,"IPrintWithWriter","IPrintWithWriter",-1205316154,null),new cljs.core.Symbol(null,"IWriter","IWriter",-1681087107,null),new cljs.core.Symbol(null,"IPrintWithWriter","IPrintWithWriter",-1205316154,null),new cljs.core.Symbol(null,"IPending","IPending",1229113039,null),new cljs.core.Symbol(null,"IWatchable","IWatchable",-1929659016,null),new cljs.core.Symbol(null,"IEditableCollection","IEditableCollection",-906687187,null),new cljs.core.Symbol(null,"ITransientCollection","ITransientCollection",252832402,null),new cljs.core.Symbol(null,"ITransientAssociative","ITransientAssociative",-1612754624,null),new cljs.core.Symbol(null,"ITransientMap","ITransientMap",298423651,null),new cljs.core.Symbol(null,"ITransientVector","ITransientVector",1978793164,null),new cljs.core.Symbol(null,"ITransientSet","ITransientSet",-575559912,null),new cljs.core.Symbol(null,"IMultiFn","IMultiFn",-1848282794,null),new cljs.core.Symbol(null,"IChunkedSeq","IChunkedSeq",-1299765705,null),new cljs.core.Symbol(null,"IChunkedNext","IChunkedNext",1193289532,null),new cljs.core.Symbol(null,"IComparable","IComparable",1834481627,null),new cljs.core.Symbol(null,"INamed","INamed",357992946,null),new cljs.core.Symbol(null,"ICloneable","ICloneable",1882653160,null),new cljs.core.Symbol(null,"IAtom","IAtom",-1411134312,null),new cljs.core.Symbol(null,"IReset","IReset",-1893729426,null),new cljs.core.Symbol(null,"ISwap","ISwap",484378193,null),new cljs.core.Symbol(null,"IIterable","IIterable",577191430,null)], true)),cljs.core.iterate((function (p__7004){
var vec__7005 = p__7004;
var p = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7005,(0),null);
var b = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7005,(1),null);
if(((2147483648) === b)){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(p + (1)),(1)], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p,((2) * b)], null);
}
}),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(0),(1)], null)));
/**
 * total number of partitions
 */
cljs.core$macros.fast_path_protocol_partitions_count = (function (){var c = cljs.core.count(cljs.core$macros.fast_path_protocols);
var m = cljs.core.mod(c,(32));
if((m === (0))){
return cljs.core.quot(c,(32));
} else {
return (cljs.core.quot(c,(32)) + (1));
}
})();
var ret__18490__auto___7011 = (function (){
cljs.core$macros.str = (function cljs$core$macros$str(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7012 = arguments.length;
var i__18451__auto___7013 = (0);
while(true){
if((i__18451__auto___7013 < len__18450__auto___7012)){
args__18453__auto__.push((arguments[i__18451__auto___7013]));

var G__7014 = (i__18451__auto___7013 + (1));
i__18451__auto___7013 = G__7014;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.str.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.str.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,xs){
var interpolate = (function (x){
if(typeof x === 'string'){
return "~{}";
} else {
return "cljs.core.str.cljs$core$IFn$_invoke$arity$1(~{})";
}
});
var strs = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.str,cljs.core.interpose.cljs$core$IFn$_invoke$arity$2(",",cljs.core.map.cljs$core$IFn$_invoke$arity$2(interpolate,xs)));
return cljs.core.list_STAR_.cljs$core$IFn$_invoke$arity$3(new cljs.core.Symbol(null,"js*","js*",-1134233646,null),["[",cljs.core.str.cljs$core$IFn$_invoke$arity$1(strs),"].join('')"].join(''),xs);
});

cljs.core$macros.str.cljs$lang$maxFixedArity = (2);

cljs.core$macros.str.cljs$lang$applyTo = (function (seq7008){
var G__7009 = cljs.core.first(seq7008);
var seq7008__$1 = cljs.core.next(seq7008);
var G__7010 = cljs.core.first(seq7008__$1);
var seq7008__$2 = cljs.core.next(seq7008__$1);
return cljs.core$macros.str.cljs$core$IFn$_invoke$arity$variadic(G__7009,G__7010,seq7008__$2);
});

return null;
})()
;
cljs.core$macros.str.cljs$lang$macro = true;

cljs.core$macros.bool_expr = (function cljs$core$macros$bool_expr(e){
return cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(e,cljs.core.assoc,new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null));
});
cljs.core$macros.simple_test_expr_QMARK_ = (function cljs$core$macros$simple_test_expr_QMARK_(env,ast){
var and__17759__auto__ = (function (){var G__7018 = new cljs.core.Keyword(null,"op","op",-1882987955).cljs$core$IFn$_invoke$arity$1(ast);
var fexpr__7017 = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"js","js",1768080579),null,new cljs.core.Keyword(null,"constant","constant",-379609303),null,new cljs.core.Keyword(null,"var","var",-769682797),null,new cljs.core.Keyword(null,"invoke","invoke",1145927159),null,new cljs.core.Keyword(null,"dot","dot",1442709401),null], null), null);
return (fexpr__7017.cljs$core$IFn$_invoke$arity$1 ? fexpr__7017.cljs$core$IFn$_invoke$arity$1(G__7018) : fexpr__7017.call(null,G__7018));
})();
if(cljs.core.truth_(and__17759__auto__)){
var G__7020 = cljs.analyzer.infer_tag(env,ast);
var fexpr__7019 = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Symbol(null,"seq","seq",-177272256,null),null,new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),null], null), null);
return (fexpr__7019.cljs$core$IFn$_invoke$arity$1 ? fexpr__7019.cljs$core$IFn$_invoke$arity$1(G__7020) : fexpr__7019.call(null,G__7020));
} else {
return and__17759__auto__;
}
});
var ret__18490__auto___7031 = (function (){
/**
 * Evaluates exprs one at a time, from left to right. If a form
 *   returns logical false (nil or false), and returns that value and
 *   doesn't evaluate any of the other expressions, otherwise it returns
 *   the value of the last expr. (and) returns true.
 */
cljs.core$macros.and = (function cljs$core$macros$and(var_args){
var G__7029 = arguments.length;
switch (G__7029) {
case 2:
return cljs.core$macros.and.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core$macros.and.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7033 = arguments.length;
var i__18451__auto___7034 = (0);
while(true){
if((i__18451__auto___7034 < len__18450__auto___7033)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7034]));

var G__7035 = (i__18451__auto___7034 + (1));
i__18451__auto___7034 = G__7035;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((3)),(0),null));
return cljs.core$macros.and.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18470__auto__);

}
});

cljs.core$macros.and.cljs$core$IFn$_invoke$arity$2 = (function (_AMPERSAND_form,_AMPERSAND_env){
return true;
});

cljs.core$macros.and.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});

cljs.core$macros.and.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,next){
var forms = cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [x], null),next);
if(cljs.core.every_QMARK_(((function (forms){
return (function (p1__7021_SHARP_){
return cljs.core$macros.simple_test_expr_QMARK_(_AMPERSAND_env,p1__7021_SHARP_);
});})(forms))
,cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (forms){
return (function (p1__7022_SHARP_){
var _STAR_cljs_warnings_STAR_7030 = cljs.analyzer._STAR_cljs_warnings_STAR_;
cljs.analyzer._STAR_cljs_warnings_STAR_ = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"invoke-ctor","invoke-ctor",-756052704),new cljs.core.Keyword(null,"extending-base-js-type","extending-base-js-type",432787264),new cljs.core.Keyword(null,"unsupported-preprocess-value","unsupported-preprocess-value",1635147105),new cljs.core.Keyword(null,"redef","redef",1032704258),new cljs.core.Keyword(null,"js-shadowed-by-local","js-shadowed-by-local",198048291),new cljs.core.Keyword(null,"unsupported-js-module-type","unsupported-js-module-type",1806206180),new cljs.core.Keyword(null,"invalid-protocol-symbol","invalid-protocol-symbol",86246948),new cljs.core.Keyword(null,"protocol-duped-method","protocol-duped-method",15128166),new cljs.core.Keyword(null,"munged-namespace","munged-namespace",-101986199),new cljs.core.Keyword(null,"single-segment-namespace","single-segment-namespace",-491924469),new cljs.core.Keyword(null,"fn-arity","fn-arity",-403576501),new cljs.core.Keyword(null,"infer-warning","infer-warning",-1600671410),new cljs.core.Keyword(null,"variadic-max-arity","variadic-max-arity",-14288402),new cljs.core.Keyword(null,"protocol-with-variadic-method","protocol-with-variadic-method",-693368178),new cljs.core.Keyword(null,"fn-deprecated","fn-deprecated",626877647),new cljs.core.Keyword(null,"undeclared-var","undeclared-var",-1624364944),new cljs.core.Keyword(null,"preamble-missing","preamble-missing",220974801),new cljs.core.Keyword(null,"undeclared-protocol-symbol","undeclared-protocol-symbol",462882867),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"invalid-array-access","invalid-array-access",-1747132236),new cljs.core.Keyword(null,"unprovided","unprovided",-652330764),new cljs.core.Keyword(null,"ns-var-clash","ns-var-clash",-280494668),new cljs.core.Keyword(null,"undeclared-ns","undeclared-ns",-1589012812),new cljs.core.Keyword(null,"undeclared-ns-form","undeclared-ns-form",-248024137),new cljs.core.Keyword(null,"invalid-arithmetic","invalid-arithmetic",1917075065),new cljs.core.Keyword(null,"protocol-deprecated","protocol-deprecated",103233497),new cljs.core.Keyword(null,"fn-var","fn-var",1086204730),new cljs.core.Keyword(null,"protocol-impl-recur-with-target","protocol-impl-recur-with-target",-1648321574),new cljs.core.Keyword(null,"overload-arity","overload-arity",823206044),new cljs.core.Keyword(null,"protocol-multiple-impls","protocol-multiple-impls",794179260),new cljs.core.Keyword(null,"redef-in-file","redef-in-file",-476530788),new cljs.core.Keyword(null,"protocol-invalid-method","protocol-invalid-method",522647516),new cljs.core.Keyword(null,"extend-type-invalid-method-shape","extend-type-invalid-method-shape",1424103549),new cljs.core.Keyword(null,"multiple-variadic-overloads","multiple-variadic-overloads",1110059837),new cljs.core.Keyword(null,"protocol-impl-with-variadic-method","protocol-impl-with-variadic-method",-319321217)],[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false]);

try{return cljs.analyzer.analyze.cljs$core$IFn$_invoke$arity$2(_AMPERSAND_env,p1__7022_SHARP_);
}finally {cljs.analyzer._STAR_cljs_warnings_STAR_ = _STAR_cljs_warnings_STAR_7030;
}});})(forms))
,forms))){
var and_str = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.str,cljs.core.interpose.cljs$core$IFn$_invoke$arity$2(" && ",cljs.core.repeat.cljs$core$IFn$_invoke$arity$2(cljs.core.count(forms),"(~{})")));
return cljs.core$macros.bool_expr(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"js*","js*",-1134233646,null)),(function (){var x__18265__auto__ = and_str;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([forms], 0)))));
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"and__7023__auto__","and__7023__auto__",-1182483090,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"and__7023__auto__","and__7023__auto__",-1182483090,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","and","cljs.core$macros/and",48320334,null)),next)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"and__7023__auto__","and__7023__auto__",-1182483090,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
}
});

cljs.core$macros.and.cljs$lang$applyTo = (function (seq7025){
var G__7026 = cljs.core.first(seq7025);
var seq7025__$1 = cljs.core.next(seq7025);
var G__7027 = cljs.core.first(seq7025__$1);
var seq7025__$2 = cljs.core.next(seq7025__$1);
var G__7028 = cljs.core.first(seq7025__$2);
var seq7025__$3 = cljs.core.next(seq7025__$2);
return cljs.core$macros.and.cljs$core$IFn$_invoke$arity$variadic(G__7026,G__7027,G__7028,seq7025__$3);
});

cljs.core$macros.and.cljs$lang$maxFixedArity = (3);

return null;
})()
;
cljs.core$macros.and.cljs$lang$macro = true;

var ret__18490__auto___7046 = (function (){
/**
 * Evaluates exprs one at a time, from left to right. If a form
 *   returns a logical true value, or returns that value and doesn't
 *   evaluate any of the other expressions, otherwise it returns the
 *   value of the last expression. (or) returns nil.
 */
cljs.core$macros.or = (function cljs$core$macros$or(var_args){
var G__7044 = arguments.length;
switch (G__7044) {
case 2:
return cljs.core$macros.or.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core$macros.or.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7048 = arguments.length;
var i__18451__auto___7049 = (0);
while(true){
if((i__18451__auto___7049 < len__18450__auto___7048)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7049]));

var G__7050 = (i__18451__auto___7049 + (1));
i__18451__auto___7049 = G__7050;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((3)),(0),null));
return cljs.core$macros.or.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18470__auto__);

}
});

cljs.core$macros.or.cljs$core$IFn$_invoke$arity$2 = (function (_AMPERSAND_form,_AMPERSAND_env){
return null;
});

cljs.core$macros.or.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});

cljs.core$macros.or.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,next){
var forms = cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [x], null),next);
if(cljs.core.every_QMARK_(((function (forms){
return (function (p1__7036_SHARP_){
return cljs.core$macros.simple_test_expr_QMARK_(_AMPERSAND_env,p1__7036_SHARP_);
});})(forms))
,cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (forms){
return (function (p1__7037_SHARP_){
var _STAR_cljs_warnings_STAR_7045 = cljs.analyzer._STAR_cljs_warnings_STAR_;
cljs.analyzer._STAR_cljs_warnings_STAR_ = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"invoke-ctor","invoke-ctor",-756052704),new cljs.core.Keyword(null,"extending-base-js-type","extending-base-js-type",432787264),new cljs.core.Keyword(null,"unsupported-preprocess-value","unsupported-preprocess-value",1635147105),new cljs.core.Keyword(null,"redef","redef",1032704258),new cljs.core.Keyword(null,"js-shadowed-by-local","js-shadowed-by-local",198048291),new cljs.core.Keyword(null,"unsupported-js-module-type","unsupported-js-module-type",1806206180),new cljs.core.Keyword(null,"invalid-protocol-symbol","invalid-protocol-symbol",86246948),new cljs.core.Keyword(null,"protocol-duped-method","protocol-duped-method",15128166),new cljs.core.Keyword(null,"munged-namespace","munged-namespace",-101986199),new cljs.core.Keyword(null,"single-segment-namespace","single-segment-namespace",-491924469),new cljs.core.Keyword(null,"fn-arity","fn-arity",-403576501),new cljs.core.Keyword(null,"infer-warning","infer-warning",-1600671410),new cljs.core.Keyword(null,"variadic-max-arity","variadic-max-arity",-14288402),new cljs.core.Keyword(null,"protocol-with-variadic-method","protocol-with-variadic-method",-693368178),new cljs.core.Keyword(null,"fn-deprecated","fn-deprecated",626877647),new cljs.core.Keyword(null,"undeclared-var","undeclared-var",-1624364944),new cljs.core.Keyword(null,"preamble-missing","preamble-missing",220974801),new cljs.core.Keyword(null,"undeclared-protocol-symbol","undeclared-protocol-symbol",462882867),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"invalid-array-access","invalid-array-access",-1747132236),new cljs.core.Keyword(null,"unprovided","unprovided",-652330764),new cljs.core.Keyword(null,"ns-var-clash","ns-var-clash",-280494668),new cljs.core.Keyword(null,"undeclared-ns","undeclared-ns",-1589012812),new cljs.core.Keyword(null,"undeclared-ns-form","undeclared-ns-form",-248024137),new cljs.core.Keyword(null,"invalid-arithmetic","invalid-arithmetic",1917075065),new cljs.core.Keyword(null,"protocol-deprecated","protocol-deprecated",103233497),new cljs.core.Keyword(null,"fn-var","fn-var",1086204730),new cljs.core.Keyword(null,"protocol-impl-recur-with-target","protocol-impl-recur-with-target",-1648321574),new cljs.core.Keyword(null,"overload-arity","overload-arity",823206044),new cljs.core.Keyword(null,"protocol-multiple-impls","protocol-multiple-impls",794179260),new cljs.core.Keyword(null,"redef-in-file","redef-in-file",-476530788),new cljs.core.Keyword(null,"protocol-invalid-method","protocol-invalid-method",522647516),new cljs.core.Keyword(null,"extend-type-invalid-method-shape","extend-type-invalid-method-shape",1424103549),new cljs.core.Keyword(null,"multiple-variadic-overloads","multiple-variadic-overloads",1110059837),new cljs.core.Keyword(null,"protocol-impl-with-variadic-method","protocol-impl-with-variadic-method",-319321217)],[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false]);

try{return cljs.analyzer.analyze.cljs$core$IFn$_invoke$arity$2(_AMPERSAND_env,p1__7037_SHARP_);
}finally {cljs.analyzer._STAR_cljs_warnings_STAR_ = _STAR_cljs_warnings_STAR_7045;
}});})(forms))
,forms))){
var or_str = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.str,cljs.core.interpose.cljs$core$IFn$_invoke$arity$2(" || ",cljs.core.repeat.cljs$core$IFn$_invoke$arity$2(cljs.core.count(forms),"(~{})")));
return cljs.core$macros.bool_expr(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"js*","js*",-1134233646,null)),(function (){var x__18265__auto__ = or_str;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([forms], 0)))));
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"or__7038__auto__","or__7038__auto__",-1644572928,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"or__7038__auto__","or__7038__auto__",-1644572928,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"or__7038__auto__","or__7038__auto__",-1644572928,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","or","cljs.core$macros/or",1346243648,null)),next)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
}
});

cljs.core$macros.or.cljs$lang$applyTo = (function (seq7040){
var G__7041 = cljs.core.first(seq7040);
var seq7040__$1 = cljs.core.next(seq7040);
var G__7042 = cljs.core.first(seq7040__$1);
var seq7040__$2 = cljs.core.next(seq7040__$1);
var G__7043 = cljs.core.first(seq7040__$2);
var seq7040__$3 = cljs.core.next(seq7040__$2);
return cljs.core$macros.or.cljs$core$IFn$_invoke$arity$variadic(G__7041,G__7042,G__7043,seq7040__$3);
});

cljs.core$macros.or.cljs$lang$maxFixedArity = (3);

return null;
})()
;
cljs.core$macros.or.cljs$lang$macro = true;

var ret__18490__auto___7051 = cljs.core$macros.nil_QMARK_ = (function cljs$core$macros$nil_QMARK_(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","coercive-=","cljs.core$macros/coercive-=",-1655776086,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
});
cljs.core$macros.nil_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7052 = cljs.core$macros.some_QMARK_ = (function cljs$core$macros$some_QMARK_(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","not","cljs.core$macros/not",325607907,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});
cljs.core$macros.some_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7053 = cljs.core$macros.coercive_not = (function cljs$core$macros$coercive_not(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),"(!~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});
cljs.core$macros.coercive_not.cljs$lang$macro = true;

var ret__18490__auto___7054 = cljs.core$macros.coercive_not_EQ_ = (function cljs$core$macros$coercive_not_EQ_(_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} != ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});
cljs.core$macros.coercive_not_EQ_.cljs$lang$macro = true;

var ret__18490__auto___7055 = cljs.core$macros.coercive__EQ_ = (function cljs$core$macros$coercive__EQ_(_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} == ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});
cljs.core$macros.coercive__EQ_.cljs$lang$macro = true;

var ret__18490__auto___7056 = cljs.core$macros.coercive_boolean = (function cljs$core$macros$coercive_boolean(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.with_meta(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),"~{}"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol(null,"boolean","boolean",-278886877,null)], null));
});
cljs.core$macros.coercive_boolean.cljs$lang$macro = true;

var ret__18490__auto___7057 = cljs.core$macros.truth_ = (function cljs$core$macros$truth_(_AMPERSAND_form,_AMPERSAND_env,x){
if((x instanceof cljs.core.Symbol)){
} else {
throw (new Error(["Assert failed: ","x is substituted twice","\n","(core/symbol? x)"].join('')));
}

return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} != null && ~{} !== false)"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.truth_.cljs$lang$macro = true;

var ret__18490__auto___7058 = cljs.core$macros.js_arguments = (function cljs$core$macros$js_arguments(_AMPERSAND_form,_AMPERSAND_env){
return cljs.core._conj(cljs.core._conj(cljs.core.List.EMPTY,"arguments"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.js_arguments.cljs$lang$macro = true;

var ret__18490__auto___7059 = cljs.core$macros.js_delete = (function cljs$core$macros$js_delete(_AMPERSAND_form,_AMPERSAND_env,obj,key){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = obj;
return cljs.core._conj((function (){var x__18265__auto____$1 = key;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"delete ~{}[~{}]"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.js_delete.cljs$lang$macro = true;

var ret__18490__auto___7060 = cljs.core$macros.js_in = (function cljs$core$macros$js_in(_AMPERSAND_form,_AMPERSAND_env,key,obj){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = key;
return cljs.core._conj((function (){var x__18265__auto____$1 = obj;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"~{} in ~{}"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.js_in.cljs$lang$macro = true;

var ret__18490__auto___7061 = /**
 * Emit JavaScript "debugger;" statement
 */
cljs.core$macros.js_debugger = (function cljs$core$macros$js_debugger(_AMPERSAND_form,_AMPERSAND_env){
return cljs.core._conj((function (){var x__18265__auto__ = cljs.core._conj(cljs.core._conj(cljs.core.List.EMPTY,"debugger"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
return cljs.core._conj(cljs.core._conj(cljs.core.List.EMPTY,null),x__18265__auto__);
})(),new cljs.core.Symbol(null,"do","do",1686842252,null));
});
cljs.core$macros.js_debugger.cljs$lang$macro = true;

var ret__18490__auto___7066 = /**
 * Emit a top-level JavaScript multi-line comment. New lines will create a
 *   new comment line. Comment block will be preceded and followed by a newline
 */
cljs.core$macros.js_comment = (function cljs$core$macros$js_comment(_AMPERSAND_form,_AMPERSAND_env,comment){
var vec__7063 = clojure.string.split.cljs$core$IFn$_invoke$arity$2(comment,/\n/);
var seq__7064 = cljs.core.seq(vec__7063);
var first__7065 = cljs.core.first(seq__7064);
var seq__7064__$1 = cljs.core.next(seq__7064);
var x = first__7065;
var ys = seq__7064__$1;
return cljs.core._conj((function (){var x__18265__auto__ = ["\n/**\n",cljs.core.str.cljs$core$IFn$_invoke$arity$1([" * ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(x),"\n"].join('')),cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(cljs.core.str,"",cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (vec__7063,seq__7064,first__7065,seq__7064__$1,x,ys){
return (function (p1__7062_SHARP_){
return [" * ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(clojure.string.replace(p1__7062_SHARP_,/^   /,"")),"\n"].join('');
});})(vec__7063,seq__7064,first__7065,seq__7064__$1,x,ys))
,ys)))," */\n"].join('');
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.js_comment.cljs$lang$macro = true;

var ret__18490__auto___7067 = /**
 * EXPERIMENTAL: Subject to change. Unsafely cast a value to a different type.
 */
cljs.core$macros.unsafe_cast = (function cljs$core$macros$unsafe_cast(_AMPERSAND_form,_AMPERSAND_env,t,x){
var cast_expr = ["~{} = /** @type {",cljs.core.str.cljs$core$IFn$_invoke$arity$1(t),"} */ (~{})"].join('');
return cljs.core._conj((function (){var x__18265__auto__ = cast_expr;
return cljs.core._conj((function (){var x__18265__auto____$1 = x;
return cljs.core._conj((function (){var x__18265__auto____$2 = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$2);
})(),x__18265__auto____$1);
})(),x__18265__auto__);
})(),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.unsafe_cast.cljs$lang$macro = true;

var ret__18490__auto___7068 = /**
 * Emit an inline JavaScript comment.
 */
cljs.core$macros.js_inline_comment = (function cljs$core$macros$js_inline_comment(_AMPERSAND_form,_AMPERSAND_env,comment){
return cljs.core._conj((function (){var x__18265__auto__ = ["/**",cljs.core.str.cljs$core$IFn$_invoke$arity$1(comment),"*/"].join('');
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.js_inline_comment.cljs$lang$macro = true;

var ret__18490__auto___7069 = cljs.core$macros.true_QMARK_ = (function cljs$core$macros$true_QMARK_(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),"~{} === true"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});
cljs.core$macros.true_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7070 = cljs.core$macros.false_QMARK_ = (function cljs$core$macros$false_QMARK_(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),"~{} === false"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});
cljs.core$macros.false_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7071 = cljs.core$macros.string_QMARK_ = (function cljs$core$macros$string_QMARK_(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),"typeof ~{} === 'string'"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});
cljs.core$macros.string_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7072 = /**
 * Return true if argument exists, analogous to usage of typeof operator
 * in JavaScript.
 */
cljs.core$macros.exists_QMARK_ = (function cljs$core$macros$exists_QMARK_(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(x,cljs.core.assoc,new cljs.core.Keyword("cljs.analyzer","no-resolve","cljs.analyzer/no-resolve",-1872351017),true);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),"typeof ~{} !== 'undefined'"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});
cljs.core$macros.exists_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7073 = /**
 * Return true if argument is identical to the JavaScript undefined value.
 */
cljs.core$macros.undefined_QMARK_ = (function cljs$core$macros$undefined_QMARK_(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),"(void 0 === ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});
cljs.core$macros.undefined_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7074 = cljs.core$macros.identical_QMARK_ = (function cljs$core$macros$identical_QMARK_(_AMPERSAND_form,_AMPERSAND_env,a,b){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = a;
return cljs.core._conj((function (){var x__18265__auto____$1 = b;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} === ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});
cljs.core$macros.identical_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7077 = cljs.core$macros.instance_QMARK_ = (function cljs$core$macros$instance_QMARK_(_AMPERSAND_form,_AMPERSAND_env,c,x){
return cljs.core$macros.bool_expr((((c instanceof cljs.core.Symbol))?cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = c;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} instanceof ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)):cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"c__7075__auto__","c__7075__auto__",554637876,null)),(function (){var x__18265__auto__ = c;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7076__auto__","x__7076__auto__",600732194,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"js*","js*",-1134233646,null)),cljs.core._conj(cljs.core.List.EMPTY,"(~{} instanceof ~{})"),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7076__auto__","x__7076__auto__",600732194,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"c__7075__auto__","c__7075__auto__",554637876,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))))));
});
cljs.core$macros.instance_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7078 = cljs.core$macros.number_QMARK_ = (function cljs$core$macros$number_QMARK_(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),"typeof ~{} === 'number'"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});
cljs.core$macros.number_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7079 = cljs.core$macros.symbol_QMARK_ = (function cljs$core$macros$symbol_QMARK_(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core$macros.bool_expr(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","instance?","cljs.core$macros/instance?",1829750179,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","Symbol","cljs.core$macros/Symbol",229570133,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
});
cljs.core$macros.symbol_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7080 = cljs.core$macros.keyword_QMARK_ = (function cljs$core$macros$keyword_QMARK_(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core$macros.bool_expr(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","instance?","cljs.core$macros/instance?",1829750179,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","Keyword","cljs.core$macros/Keyword",-373341625,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
});
cljs.core$macros.keyword_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7090 = (function (){
cljs.core$macros.aget = (function cljs$core$macros$aget(var_args){
var G__7087 = arguments.length;
switch (G__7087) {
case 4:
return cljs.core$macros.aget.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7092 = arguments.length;
var i__18451__auto___7093 = (0);
while(true){
if((i__18451__auto___7093 < len__18450__auto___7092)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7093]));

var G__7094 = (i__18451__auto___7093 + (1));
i__18451__auto___7093 = G__7094;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros.aget.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros.aget.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,array,idx){
var G__7088 = cljs.analyzer.checked_arrays();
var G__7088__$1 = (((G__7088 instanceof cljs.core.Keyword))?G__7088.fqn:null);
switch (G__7088__$1) {
case "warn":
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","checked-aget","cljs.core$macros/checked-aget",499526574,null)),(function (){var x__18265__auto__ = array;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));

break;
case "error":
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","checked-aget'","cljs.core$macros/checked-aget'",1805231826,null)),(function (){var x__18265__auto__ = array;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));

break;
default:
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = array;
return cljs.core._conj((function (){var x__18265__auto____$1 = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{}[~{}])"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));

}
});

cljs.core$macros.aget.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,array,idx,idxs){
var G__7089 = cljs.analyzer.checked_arrays();
var G__7089__$1 = (((G__7089 instanceof cljs.core.Keyword))?G__7089.fqn:null);
switch (G__7089__$1) {
case "warn":
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","checked-aget","cljs.core$macros/checked-aget",499526574,null)),(function (){var x__18265__auto__ = array;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),idxs], 0))));

break;
case "error":
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","checked-aget'","cljs.core$macros/checked-aget'",1805231826,null)),(function (){var x__18265__auto__ = array;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),idxs], 0))));

break;
default:
var astr = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.str,cljs.core.repeat.cljs$core$IFn$_invoke$arity$2(cljs.core.count(idxs),"[~{}]"));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"js*","js*",-1134233646,null)),(function (){var x__18265__auto__ = ["(~{}[~{}]",cljs.core.str.cljs$core$IFn$_invoke$arity$1(astr),")"].join('');
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = array;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),idxs], 0))));

}
});

cljs.core$macros.aget.cljs$lang$applyTo = (function (seq7082){
var G__7083 = cljs.core.first(seq7082);
var seq7082__$1 = cljs.core.next(seq7082);
var G__7084 = cljs.core.first(seq7082__$1);
var seq7082__$2 = cljs.core.next(seq7082__$1);
var G__7085 = cljs.core.first(seq7082__$2);
var seq7082__$3 = cljs.core.next(seq7082__$2);
var G__7086 = cljs.core.first(seq7082__$3);
var seq7082__$4 = cljs.core.next(seq7082__$3);
return cljs.core$macros.aget.cljs$core$IFn$_invoke$arity$variadic(G__7083,G__7084,G__7085,G__7086,seq7082__$4);
});

cljs.core$macros.aget.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros.aget.cljs$lang$macro = true;

var ret__18490__auto___7107 = (function (){
cljs.core$macros.aset = (function cljs$core$macros$aset(var_args){
var G__7104 = arguments.length;
switch (G__7104) {
case 5:
return cljs.core$macros.aset.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7109 = arguments.length;
var i__18451__auto___7110 = (0);
while(true){
if((i__18451__auto___7110 < len__18450__auto___7109)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7110]));

var G__7111 = (i__18451__auto___7110 + (1));
i__18451__auto___7110 = G__7111;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((5)),(0),null));
return cljs.core$macros.aset.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),argseq__18470__auto__);

}
});

cljs.core$macros.aset.cljs$core$IFn$_invoke$arity$5 = (function (_AMPERSAND_form,_AMPERSAND_env,array,idx,val){
var G__7105 = cljs.analyzer.checked_arrays();
var G__7105__$1 = (((G__7105 instanceof cljs.core.Keyword))?G__7105.fqn:null);
switch (G__7105__$1) {
case "warn":
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","checked-aset","cljs.core$macros/checked-aset",-1311078680,null)),(function (){var x__18265__auto__ = array;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = val;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));

break;
case "error":
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","checked-aset'","cljs.core$macros/checked-aset'",515643223,null)),(function (){var x__18265__auto__ = array;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = val;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));

break;
default:
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = array;
return cljs.core._conj((function (){var x__18265__auto____$1 = idx;
return cljs.core._conj((function (){var x__18265__auto____$2 = val;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$2);
})(),x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{}[~{}] = ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));

}
});

cljs.core$macros.aset.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,array,idx,idx2,idxv){
var G__7106 = cljs.analyzer.checked_arrays();
var G__7106__$1 = (((G__7106 instanceof cljs.core.Keyword))?G__7106.fqn:null);
switch (G__7106__$1) {
case "warn":
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","checked-aset","cljs.core$macros/checked-aset",-1311078680,null)),(function (){var x__18265__auto__ = array;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = idx2;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),idxv], 0))));

break;
case "error":
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","checked-aset'","cljs.core$macros/checked-aset'",515643223,null)),(function (){var x__18265__auto__ = array;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = idx2;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),idxv], 0))));

break;
default:
var n = (cljs.core.count(idxv) - (1));
var astr = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.str,cljs.core.repeat.cljs$core$IFn$_invoke$arity$2(n,"[~{}]"));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"js*","js*",-1134233646,null)),(function (){var x__18265__auto__ = ["(~{}[~{}][~{}]",cljs.core.str.cljs$core$IFn$_invoke$arity$1(astr)," = ~{})"].join('');
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = array;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = idx2;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),idxv], 0))));

}
});

cljs.core$macros.aset.cljs$lang$applyTo = (function (seq7098){
var G__7099 = cljs.core.first(seq7098);
var seq7098__$1 = cljs.core.next(seq7098);
var G__7100 = cljs.core.first(seq7098__$1);
var seq7098__$2 = cljs.core.next(seq7098__$1);
var G__7101 = cljs.core.first(seq7098__$2);
var seq7098__$3 = cljs.core.next(seq7098__$2);
var G__7102 = cljs.core.first(seq7098__$3);
var seq7098__$4 = cljs.core.next(seq7098__$3);
var G__7103 = cljs.core.first(seq7098__$4);
var seq7098__$5 = cljs.core.next(seq7098__$4);
return cljs.core$macros.aset.cljs$core$IFn$_invoke$arity$variadic(G__7099,G__7100,G__7101,G__7102,G__7103,seq7098__$5);
});

cljs.core$macros.aset.cljs$lang$maxFixedArity = (5);

return null;
})()
;
cljs.core$macros.aset.cljs$lang$macro = true;

var ret__18490__auto___7114 = /**
 * INTERNAL. Compiles to JavaScript property access using bracket notation. Does
 *   not distinguish between object and array types and not subject to compiler
 *   static analysis.
 */
cljs.core$macros.unchecked_get = (function cljs$core$macros$unchecked_get(_AMPERSAND_form,_AMPERSAND_env,obj,key){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = obj;
return cljs.core._conj((function (){var x__18265__auto____$1 = key;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{}[~{}])"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.unchecked_get.cljs$lang$macro = true;

var ret__18490__auto___7115 = /**
 * INTERNAL. Compiles to JavaScript property access using bracket notation. Does
 *   not distinguish between object and array types and not subject to compiler
 *   static analysis.
 */
cljs.core$macros.unchecked_set = (function cljs$core$macros$unchecked_set(_AMPERSAND_form,_AMPERSAND_env,obj,key,val){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = obj;
return cljs.core._conj((function (){var x__18265__auto____$1 = key;
return cljs.core._conj((function (){var x__18265__auto____$2 = val;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$2);
})(),x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{}[~{}] = ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.unchecked_set.cljs$lang$macro = true;

var ret__18490__auto___7123 = (function (){
cljs.core$macros._PLUS_ = (function cljs$core$macros$_PLUS_(var_args){
var G__7122 = arguments.length;
switch (G__7122) {
case 2:
return cljs.core$macros._PLUS_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core$macros._PLUS_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros._PLUS_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7125 = arguments.length;
var i__18451__auto___7126 = (0);
while(true){
if((i__18451__auto___7126 < len__18450__auto___7125)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7126]));

var G__7127 = (i__18451__auto___7126 + (1));
i__18451__auto___7126 = G__7127;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros._PLUS_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros._PLUS_.cljs$core$IFn$_invoke$arity$2 = (function (_AMPERSAND_form,_AMPERSAND_env){
return (0);
});

cljs.core$macros._PLUS_.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});

cljs.core$macros._PLUS_.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} + ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});

cljs.core$macros._PLUS_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","+","cljs.core$macros/+",-18260342,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","+","cljs.core$macros/+",-18260342,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
});

cljs.core$macros._PLUS_.cljs$lang$applyTo = (function (seq7117){
var G__7118 = cljs.core.first(seq7117);
var seq7117__$1 = cljs.core.next(seq7117);
var G__7119 = cljs.core.first(seq7117__$1);
var seq7117__$2 = cljs.core.next(seq7117__$1);
var G__7120 = cljs.core.first(seq7117__$2);
var seq7117__$3 = cljs.core.next(seq7117__$2);
var G__7121 = cljs.core.first(seq7117__$3);
var seq7117__$4 = cljs.core.next(seq7117__$3);
return cljs.core$macros._PLUS_.cljs$core$IFn$_invoke$arity$variadic(G__7118,G__7119,G__7120,G__7121,seq7117__$4);
});

cljs.core$macros._PLUS_.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros._PLUS_.cljs$lang$macro = true;

var ret__18490__auto___7128 = cljs.core$macros.byte$ = (function cljs$core$macros$byte(_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});
cljs.core$macros.byte$.cljs$lang$macro = true;

var ret__18490__auto___7129 = cljs.core$macros.short$ = (function cljs$core$macros$short(_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});
cljs.core$macros.short$.cljs$lang$macro = true;

var ret__18490__auto___7130 = cljs.core$macros.float$ = (function cljs$core$macros$float(_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});
cljs.core$macros.float$.cljs$lang$macro = true;

var ret__18490__auto___7131 = cljs.core$macros.double$ = (function cljs$core$macros$double(_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});
cljs.core$macros.double$.cljs$lang$macro = true;

var ret__18490__auto___7132 = cljs.core$macros.unchecked_byte = (function cljs$core$macros$unchecked_byte(_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});
cljs.core$macros.unchecked_byte.cljs$lang$macro = true;

var ret__18490__auto___7133 = cljs.core$macros.unchecked_char = (function cljs$core$macros$unchecked_char(_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});
cljs.core$macros.unchecked_char.cljs$lang$macro = true;

var ret__18490__auto___7134 = cljs.core$macros.unchecked_short = (function cljs$core$macros$unchecked_short(_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});
cljs.core$macros.unchecked_short.cljs$lang$macro = true;

var ret__18490__auto___7135 = cljs.core$macros.unchecked_float = (function cljs$core$macros$unchecked_float(_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});
cljs.core$macros.unchecked_float.cljs$lang$macro = true;

var ret__18490__auto___7136 = cljs.core$macros.unchecked_double = (function cljs$core$macros$unchecked_double(_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});
cljs.core$macros.unchecked_double.cljs$lang$macro = true;

var ret__18490__auto___7140 = (function (){
cljs.core$macros.unchecked_add = (function cljs$core$macros$unchecked_add(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7141 = arguments.length;
var i__18451__auto___7142 = (0);
while(true){
if((i__18451__auto___7142 < len__18450__auto___7141)){
args__18453__auto__.push((arguments[i__18451__auto___7142]));

var G__7143 = (i__18451__auto___7142 + (1));
i__18451__auto___7142 = G__7143;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.unchecked_add.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.unchecked_add.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,xs){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","+","cljs.core$macros/+",-18260342,null)),xs)));
});

cljs.core$macros.unchecked_add.cljs$lang$maxFixedArity = (2);

cljs.core$macros.unchecked_add.cljs$lang$applyTo = (function (seq7137){
var G__7138 = cljs.core.first(seq7137);
var seq7137__$1 = cljs.core.next(seq7137);
var G__7139 = cljs.core.first(seq7137__$1);
var seq7137__$2 = cljs.core.next(seq7137__$1);
return cljs.core$macros.unchecked_add.cljs$core$IFn$_invoke$arity$variadic(G__7138,G__7139,seq7137__$2);
});

return null;
})()
;
cljs.core$macros.unchecked_add.cljs$lang$macro = true;

var ret__18490__auto___7147 = (function (){
cljs.core$macros.unchecked_add_int = (function cljs$core$macros$unchecked_add_int(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7148 = arguments.length;
var i__18451__auto___7149 = (0);
while(true){
if((i__18451__auto___7149 < len__18450__auto___7148)){
args__18453__auto__.push((arguments[i__18451__auto___7149]));

var G__7150 = (i__18451__auto___7149 + (1));
i__18451__auto___7149 = G__7150;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.unchecked_add_int.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.unchecked_add_int.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,xs){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","+","cljs.core$macros/+",-18260342,null)),xs)));
});

cljs.core$macros.unchecked_add_int.cljs$lang$maxFixedArity = (2);

cljs.core$macros.unchecked_add_int.cljs$lang$applyTo = (function (seq7144){
var G__7145 = cljs.core.first(seq7144);
var seq7144__$1 = cljs.core.next(seq7144);
var G__7146 = cljs.core.first(seq7144__$1);
var seq7144__$2 = cljs.core.next(seq7144__$1);
return cljs.core$macros.unchecked_add_int.cljs$core$IFn$_invoke$arity$variadic(G__7145,G__7146,seq7144__$2);
});

return null;
})()
;
cljs.core$macros.unchecked_add_int.cljs$lang$macro = true;

var ret__18490__auto___7151 = cljs.core$macros.unchecked_dec = (function cljs$core$macros$unchecked_dec(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","dec","cljs.core$macros/dec",-247694061,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});
cljs.core$macros.unchecked_dec.cljs$lang$macro = true;

var ret__18490__auto___7152 = cljs.core$macros.unchecked_dec_int = (function cljs$core$macros$unchecked_dec_int(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","dec","cljs.core$macros/dec",-247694061,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});
cljs.core$macros.unchecked_dec_int.cljs$lang$macro = true;

var ret__18490__auto___7156 = (function (){
cljs.core$macros.unchecked_divide_int = (function cljs$core$macros$unchecked_divide_int(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7157 = arguments.length;
var i__18451__auto___7158 = (0);
while(true){
if((i__18451__auto___7158 < len__18450__auto___7157)){
args__18453__auto__.push((arguments[i__18451__auto___7158]));

var G__7159 = (i__18451__auto___7158 + (1));
i__18451__auto___7158 = G__7159;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.unchecked_divide_int.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.unchecked_divide_int.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,xs){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","/","cljs.core$macros//",-893374331,null)),xs)));
});

cljs.core$macros.unchecked_divide_int.cljs$lang$maxFixedArity = (2);

cljs.core$macros.unchecked_divide_int.cljs$lang$applyTo = (function (seq7153){
var G__7154 = cljs.core.first(seq7153);
var seq7153__$1 = cljs.core.next(seq7153);
var G__7155 = cljs.core.first(seq7153__$1);
var seq7153__$2 = cljs.core.next(seq7153__$1);
return cljs.core$macros.unchecked_divide_int.cljs$core$IFn$_invoke$arity$variadic(G__7154,G__7155,seq7153__$2);
});

return null;
})()
;
cljs.core$macros.unchecked_divide_int.cljs$lang$macro = true;

var ret__18490__auto___7160 = cljs.core$macros.unchecked_inc = (function cljs$core$macros$unchecked_inc(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","inc","cljs.core$macros/inc",876629257,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});
cljs.core$macros.unchecked_inc.cljs$lang$macro = true;

var ret__18490__auto___7161 = cljs.core$macros.unchecked_inc_int = (function cljs$core$macros$unchecked_inc_int(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","inc","cljs.core$macros/inc",876629257,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});
cljs.core$macros.unchecked_inc_int.cljs$lang$macro = true;

var ret__18490__auto___7165 = (function (){
cljs.core$macros.unchecked_multiply = (function cljs$core$macros$unchecked_multiply(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7166 = arguments.length;
var i__18451__auto___7167 = (0);
while(true){
if((i__18451__auto___7167 < len__18450__auto___7166)){
args__18453__auto__.push((arguments[i__18451__auto___7167]));

var G__7168 = (i__18451__auto___7167 + (1));
i__18451__auto___7167 = G__7168;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.unchecked_multiply.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.unchecked_multiply.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,xs){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","*","cljs.core$macros/*",946321529,null)),xs)));
});

cljs.core$macros.unchecked_multiply.cljs$lang$maxFixedArity = (2);

cljs.core$macros.unchecked_multiply.cljs$lang$applyTo = (function (seq7162){
var G__7163 = cljs.core.first(seq7162);
var seq7162__$1 = cljs.core.next(seq7162);
var G__7164 = cljs.core.first(seq7162__$1);
var seq7162__$2 = cljs.core.next(seq7162__$1);
return cljs.core$macros.unchecked_multiply.cljs$core$IFn$_invoke$arity$variadic(G__7163,G__7164,seq7162__$2);
});

return null;
})()
;
cljs.core$macros.unchecked_multiply.cljs$lang$macro = true;

var ret__18490__auto___7172 = (function (){
cljs.core$macros.unchecked_multiply_int = (function cljs$core$macros$unchecked_multiply_int(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7173 = arguments.length;
var i__18451__auto___7174 = (0);
while(true){
if((i__18451__auto___7174 < len__18450__auto___7173)){
args__18453__auto__.push((arguments[i__18451__auto___7174]));

var G__7175 = (i__18451__auto___7174 + (1));
i__18451__auto___7174 = G__7175;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.unchecked_multiply_int.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.unchecked_multiply_int.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,xs){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","*","cljs.core$macros/*",946321529,null)),xs)));
});

cljs.core$macros.unchecked_multiply_int.cljs$lang$maxFixedArity = (2);

cljs.core$macros.unchecked_multiply_int.cljs$lang$applyTo = (function (seq7169){
var G__7170 = cljs.core.first(seq7169);
var seq7169__$1 = cljs.core.next(seq7169);
var G__7171 = cljs.core.first(seq7169__$1);
var seq7169__$2 = cljs.core.next(seq7169__$1);
return cljs.core$macros.unchecked_multiply_int.cljs$core$IFn$_invoke$arity$variadic(G__7170,G__7171,seq7169__$2);
});

return null;
})()
;
cljs.core$macros.unchecked_multiply_int.cljs$lang$macro = true;

var ret__18490__auto___7176 = cljs.core$macros.unchecked_negate = (function cljs$core$macros$unchecked_negate(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-","cljs.core$macros/-",13526976,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});
cljs.core$macros.unchecked_negate.cljs$lang$macro = true;

var ret__18490__auto___7177 = cljs.core$macros.unchecked_negate_int = (function cljs$core$macros$unchecked_negate_int(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-","cljs.core$macros/-",13526976,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});
cljs.core$macros.unchecked_negate_int.cljs$lang$macro = true;

var ret__18490__auto___7178 = cljs.core$macros.unchecked_remainder_int = (function cljs$core$macros$unchecked_remainder_int(_AMPERSAND_form,_AMPERSAND_env,x,n){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","mod","cljs.core/mod",1925370196,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = n;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.unchecked_remainder_int.cljs$lang$macro = true;

var ret__18490__auto___7182 = (function (){
cljs.core$macros.unchecked_subtract = (function cljs$core$macros$unchecked_subtract(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7183 = arguments.length;
var i__18451__auto___7184 = (0);
while(true){
if((i__18451__auto___7184 < len__18450__auto___7183)){
args__18453__auto__.push((arguments[i__18451__auto___7184]));

var G__7185 = (i__18451__auto___7184 + (1));
i__18451__auto___7184 = G__7185;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.unchecked_subtract.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.unchecked_subtract.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,xs){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-","cljs.core$macros/-",13526976,null)),xs)));
});

cljs.core$macros.unchecked_subtract.cljs$lang$maxFixedArity = (2);

cljs.core$macros.unchecked_subtract.cljs$lang$applyTo = (function (seq7179){
var G__7180 = cljs.core.first(seq7179);
var seq7179__$1 = cljs.core.next(seq7179);
var G__7181 = cljs.core.first(seq7179__$1);
var seq7179__$2 = cljs.core.next(seq7179__$1);
return cljs.core$macros.unchecked_subtract.cljs$core$IFn$_invoke$arity$variadic(G__7180,G__7181,seq7179__$2);
});

return null;
})()
;
cljs.core$macros.unchecked_subtract.cljs$lang$macro = true;

var ret__18490__auto___7189 = (function (){
cljs.core$macros.unchecked_subtract_int = (function cljs$core$macros$unchecked_subtract_int(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7190 = arguments.length;
var i__18451__auto___7191 = (0);
while(true){
if((i__18451__auto___7191 < len__18450__auto___7190)){
args__18453__auto__.push((arguments[i__18451__auto___7191]));

var G__7192 = (i__18451__auto___7191 + (1));
i__18451__auto___7191 = G__7192;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.unchecked_subtract_int.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.unchecked_subtract_int.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,xs){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-","cljs.core$macros/-",13526976,null)),xs)));
});

cljs.core$macros.unchecked_subtract_int.cljs$lang$maxFixedArity = (2);

cljs.core$macros.unchecked_subtract_int.cljs$lang$applyTo = (function (seq7186){
var G__7187 = cljs.core.first(seq7186);
var seq7186__$1 = cljs.core.next(seq7186);
var G__7188 = cljs.core.first(seq7186__$1);
var seq7186__$2 = cljs.core.next(seq7186__$1);
return cljs.core$macros.unchecked_subtract_int.cljs$core$IFn$_invoke$arity$variadic(G__7187,G__7188,seq7186__$2);
});

return null;
})()
;
cljs.core$macros.unchecked_subtract_int.cljs$lang$macro = true;

var ret__18490__auto___7200 = (function (){
cljs.core$macros._ = (function cljs$core$macros$_(var_args){
var G__7199 = arguments.length;
switch (G__7199) {
case 3:
return cljs.core$macros._.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros._.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7202 = arguments.length;
var i__18451__auto___7203 = (0);
while(true){
if((i__18451__auto___7203 < len__18450__auto___7202)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7203]));

var G__7204 = (i__18451__auto___7203 + (1));
i__18451__auto___7203 = G__7204;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros._.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros._.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),"(- ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});

cljs.core$macros._.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} - ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});

cljs.core$macros._.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-","cljs.core$macros/-",13526976,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-","cljs.core$macros/-",13526976,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
});

cljs.core$macros._.cljs$lang$applyTo = (function (seq7194){
var G__7195 = cljs.core.first(seq7194);
var seq7194__$1 = cljs.core.next(seq7194);
var G__7196 = cljs.core.first(seq7194__$1);
var seq7194__$2 = cljs.core.next(seq7194__$1);
var G__7197 = cljs.core.first(seq7194__$2);
var seq7194__$3 = cljs.core.next(seq7194__$2);
var G__7198 = cljs.core.first(seq7194__$3);
var seq7194__$4 = cljs.core.next(seq7194__$3);
return cljs.core$macros._.cljs$core$IFn$_invoke$arity$variadic(G__7195,G__7196,G__7197,G__7198,seq7194__$4);
});

cljs.core$macros._.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros._.cljs$lang$macro = true;

var ret__18490__auto___7212 = (function (){
cljs.core$macros._STAR_ = (function cljs$core$macros$_STAR_(var_args){
var G__7211 = arguments.length;
switch (G__7211) {
case 2:
return cljs.core$macros._STAR_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core$macros._STAR_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros._STAR_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7214 = arguments.length;
var i__18451__auto___7215 = (0);
while(true){
if((i__18451__auto___7215 < len__18450__auto___7214)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7215]));

var G__7216 = (i__18451__auto___7215 + (1));
i__18451__auto___7215 = G__7216;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros._STAR_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros._STAR_.cljs$core$IFn$_invoke$arity$2 = (function (_AMPERSAND_form,_AMPERSAND_env){
return (1);
});

cljs.core$macros._STAR_.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});

cljs.core$macros._STAR_.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} * ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});

cljs.core$macros._STAR_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","*","cljs.core$macros/*",946321529,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","*","cljs.core$macros/*",946321529,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
});

cljs.core$macros._STAR_.cljs$lang$applyTo = (function (seq7206){
var G__7207 = cljs.core.first(seq7206);
var seq7206__$1 = cljs.core.next(seq7206);
var G__7208 = cljs.core.first(seq7206__$1);
var seq7206__$2 = cljs.core.next(seq7206__$1);
var G__7209 = cljs.core.first(seq7206__$2);
var seq7206__$3 = cljs.core.next(seq7206__$2);
var G__7210 = cljs.core.first(seq7206__$3);
var seq7206__$4 = cljs.core.next(seq7206__$3);
return cljs.core$macros._STAR_.cljs$core$IFn$_invoke$arity$variadic(G__7207,G__7208,G__7209,G__7210,seq7206__$4);
});

cljs.core$macros._STAR_.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros._STAR_.cljs$lang$macro = true;

var ret__18490__auto___7224 = (function (){
cljs.core$macros._SLASH_ = (function cljs$core$macros$_SLASH_(var_args){
var G__7223 = arguments.length;
switch (G__7223) {
case 3:
return cljs.core$macros._SLASH_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros._SLASH_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7226 = arguments.length;
var i__18451__auto___7227 = (0);
while(true){
if((i__18451__auto___7227 < len__18450__auto___7226)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7227]));

var G__7228 = (i__18451__auto___7227 + (1));
i__18451__auto___7227 = G__7228;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros._SLASH_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros._SLASH_.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","/","cljs.core$macros//",-893374331,null)),cljs.core._conj(cljs.core.List.EMPTY,(1)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros._SLASH_.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} / ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});

cljs.core$macros._SLASH_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","/","cljs.core$macros//",-893374331,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","/","cljs.core$macros//",-893374331,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
});

cljs.core$macros._SLASH_.cljs$lang$applyTo = (function (seq7218){
var G__7219 = cljs.core.first(seq7218);
var seq7218__$1 = cljs.core.next(seq7218);
var G__7220 = cljs.core.first(seq7218__$1);
var seq7218__$2 = cljs.core.next(seq7218__$1);
var G__7221 = cljs.core.first(seq7218__$2);
var seq7218__$3 = cljs.core.next(seq7218__$2);
var G__7222 = cljs.core.first(seq7218__$3);
var seq7218__$4 = cljs.core.next(seq7218__$3);
return cljs.core$macros._SLASH_.cljs$core$IFn$_invoke$arity$variadic(G__7219,G__7220,G__7221,G__7222,seq7218__$4);
});

cljs.core$macros._SLASH_.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros._SLASH_.cljs$lang$macro = true;

var ret__18490__auto___7236 = (function (){
cljs.core$macros.divide = (function cljs$core$macros$divide(var_args){
var G__7235 = arguments.length;
switch (G__7235) {
case 3:
return cljs.core$macros.divide.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros.divide.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7238 = arguments.length;
var i__18451__auto___7239 = (0);
while(true){
if((i__18451__auto___7239 < len__18450__auto___7238)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7239]));

var G__7240 = (i__18451__auto___7239 + (1));
i__18451__auto___7239 = G__7240;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros.divide.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros.divide.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","/","cljs.core$macros//",-893374331,null)),cljs.core._conj(cljs.core.List.EMPTY,(1)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.divide.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} / ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});

cljs.core$macros.divide.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","/","cljs.core$macros//",-893374331,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","/","cljs.core$macros//",-893374331,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
});

cljs.core$macros.divide.cljs$lang$applyTo = (function (seq7230){
var G__7231 = cljs.core.first(seq7230);
var seq7230__$1 = cljs.core.next(seq7230);
var G__7232 = cljs.core.first(seq7230__$1);
var seq7230__$2 = cljs.core.next(seq7230__$1);
var G__7233 = cljs.core.first(seq7230__$2);
var seq7230__$3 = cljs.core.next(seq7230__$2);
var G__7234 = cljs.core.first(seq7230__$3);
var seq7230__$4 = cljs.core.next(seq7230__$3);
return cljs.core$macros.divide.cljs$core$IFn$_invoke$arity$variadic(G__7231,G__7232,G__7233,G__7234,seq7230__$4);
});

cljs.core$macros.divide.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros.divide.cljs$lang$macro = true;

var ret__18490__auto___7248 = (function (){
cljs.core$macros._LT_ = (function cljs$core$macros$_LT_(var_args){
var G__7247 = arguments.length;
switch (G__7247) {
case 3:
return cljs.core$macros._LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros._LT_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7250 = arguments.length;
var i__18451__auto___7251 = (0);
while(true){
if((i__18451__auto___7251 < len__18450__auto___7250)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7251]));

var G__7252 = (i__18451__auto___7251 + (1));
i__18451__auto___7251 = G__7252;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros._LT_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros._LT_.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return true;
});

cljs.core$macros._LT_.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} < ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});

cljs.core$macros._LT_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","and","cljs.core$macros/and",48320334,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","<","cljs.core$macros/<",371512596,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","<","cljs.core$macros/<",371512596,null)),(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros._LT_.cljs$lang$applyTo = (function (seq7242){
var G__7243 = cljs.core.first(seq7242);
var seq7242__$1 = cljs.core.next(seq7242);
var G__7244 = cljs.core.first(seq7242__$1);
var seq7242__$2 = cljs.core.next(seq7242__$1);
var G__7245 = cljs.core.first(seq7242__$2);
var seq7242__$3 = cljs.core.next(seq7242__$2);
var G__7246 = cljs.core.first(seq7242__$3);
var seq7242__$4 = cljs.core.next(seq7242__$3);
return cljs.core$macros._LT_.cljs$core$IFn$_invoke$arity$variadic(G__7243,G__7244,G__7245,G__7246,seq7242__$4);
});

cljs.core$macros._LT_.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros._LT_.cljs$lang$macro = true;

var ret__18490__auto___7260 = (function (){
cljs.core$macros._LT__EQ_ = (function cljs$core$macros$_LT__EQ_(var_args){
var G__7259 = arguments.length;
switch (G__7259) {
case 3:
return cljs.core$macros._LT__EQ_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros._LT__EQ_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7262 = arguments.length;
var i__18451__auto___7263 = (0);
while(true){
if((i__18451__auto___7263 < len__18450__auto___7262)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7263]));

var G__7264 = (i__18451__auto___7263 + (1));
i__18451__auto___7263 = G__7264;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros._LT__EQ_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros._LT__EQ_.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return true;
});

cljs.core$macros._LT__EQ_.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} <= ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});

cljs.core$macros._LT__EQ_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","and","cljs.core$macros/and",48320334,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","<=","cljs.core$macros/<=",1865244377,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","<=","cljs.core$macros/<=",1865244377,null)),(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros._LT__EQ_.cljs$lang$applyTo = (function (seq7254){
var G__7255 = cljs.core.first(seq7254);
var seq7254__$1 = cljs.core.next(seq7254);
var G__7256 = cljs.core.first(seq7254__$1);
var seq7254__$2 = cljs.core.next(seq7254__$1);
var G__7257 = cljs.core.first(seq7254__$2);
var seq7254__$3 = cljs.core.next(seq7254__$2);
var G__7258 = cljs.core.first(seq7254__$3);
var seq7254__$4 = cljs.core.next(seq7254__$3);
return cljs.core$macros._LT__EQ_.cljs$core$IFn$_invoke$arity$variadic(G__7255,G__7256,G__7257,G__7258,seq7254__$4);
});

cljs.core$macros._LT__EQ_.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros._LT__EQ_.cljs$lang$macro = true;

var ret__18490__auto___7272 = (function (){
cljs.core$macros._GT_ = (function cljs$core$macros$_GT_(var_args){
var G__7271 = arguments.length;
switch (G__7271) {
case 3:
return cljs.core$macros._GT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros._GT_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7274 = arguments.length;
var i__18451__auto___7275 = (0);
while(true){
if((i__18451__auto___7275 < len__18450__auto___7274)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7275]));

var G__7276 = (i__18451__auto___7275 + (1));
i__18451__auto___7275 = G__7276;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros._GT_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros._GT_.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return true;
});

cljs.core$macros._GT_.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} > ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});

cljs.core$macros._GT_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","and","cljs.core$macros/and",48320334,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros",">","cljs.core$macros/>",1703297853,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros",">","cljs.core$macros/>",1703297853,null)),(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros._GT_.cljs$lang$applyTo = (function (seq7266){
var G__7267 = cljs.core.first(seq7266);
var seq7266__$1 = cljs.core.next(seq7266);
var G__7268 = cljs.core.first(seq7266__$1);
var seq7266__$2 = cljs.core.next(seq7266__$1);
var G__7269 = cljs.core.first(seq7266__$2);
var seq7266__$3 = cljs.core.next(seq7266__$2);
var G__7270 = cljs.core.first(seq7266__$3);
var seq7266__$4 = cljs.core.next(seq7266__$3);
return cljs.core$macros._GT_.cljs$core$IFn$_invoke$arity$variadic(G__7267,G__7268,G__7269,G__7270,seq7266__$4);
});

cljs.core$macros._GT_.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros._GT_.cljs$lang$macro = true;

var ret__18490__auto___7284 = (function (){
cljs.core$macros._GT__EQ_ = (function cljs$core$macros$_GT__EQ_(var_args){
var G__7283 = arguments.length;
switch (G__7283) {
case 3:
return cljs.core$macros._GT__EQ_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros._GT__EQ_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7286 = arguments.length;
var i__18451__auto___7287 = (0);
while(true){
if((i__18451__auto___7287 < len__18450__auto___7286)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7287]));

var G__7288 = (i__18451__auto___7287 + (1));
i__18451__auto___7287 = G__7288;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros._GT__EQ_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros._GT__EQ_.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return true;
});

cljs.core$macros._GT__EQ_.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} >= ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});

cljs.core$macros._GT__EQ_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","and","cljs.core$macros/and",48320334,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros",">=","cljs.core$macros/>=",527849062,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros",">=","cljs.core$macros/>=",527849062,null)),(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros._GT__EQ_.cljs$lang$applyTo = (function (seq7278){
var G__7279 = cljs.core.first(seq7278);
var seq7278__$1 = cljs.core.next(seq7278);
var G__7280 = cljs.core.first(seq7278__$1);
var seq7278__$2 = cljs.core.next(seq7278__$1);
var G__7281 = cljs.core.first(seq7278__$2);
var seq7278__$3 = cljs.core.next(seq7278__$2);
var G__7282 = cljs.core.first(seq7278__$3);
var seq7278__$4 = cljs.core.next(seq7278__$3);
return cljs.core$macros._GT__EQ_.cljs$core$IFn$_invoke$arity$variadic(G__7279,G__7280,G__7281,G__7282,seq7278__$4);
});

cljs.core$macros._GT__EQ_.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros._GT__EQ_.cljs$lang$macro = true;

var ret__18490__auto___7296 = (function (){
cljs.core$macros._EQ__EQ_ = (function cljs$core$macros$_EQ__EQ_(var_args){
var G__7295 = arguments.length;
switch (G__7295) {
case 3:
return cljs.core$macros._EQ__EQ_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros._EQ__EQ_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7298 = arguments.length;
var i__18451__auto___7299 = (0);
while(true){
if((i__18451__auto___7299 < len__18450__auto___7298)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7299]));

var G__7300 = (i__18451__auto___7299 + (1));
i__18451__auto___7299 = G__7300;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros._EQ__EQ_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros._EQ__EQ_.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return true;
});

cljs.core$macros._EQ__EQ_.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} === ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});

cljs.core$macros._EQ__EQ_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","and","cljs.core$macros/and",48320334,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","==","cljs.core$macros/==",-818551413,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","==","cljs.core$macros/==",-818551413,null)),(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros._EQ__EQ_.cljs$lang$applyTo = (function (seq7290){
var G__7291 = cljs.core.first(seq7290);
var seq7290__$1 = cljs.core.next(seq7290);
var G__7292 = cljs.core.first(seq7290__$1);
var seq7290__$2 = cljs.core.next(seq7290__$1);
var G__7293 = cljs.core.first(seq7290__$2);
var seq7290__$3 = cljs.core.next(seq7290__$2);
var G__7294 = cljs.core.first(seq7290__$3);
var seq7290__$4 = cljs.core.next(seq7290__$3);
return cljs.core$macros._EQ__EQ_.cljs$core$IFn$_invoke$arity$variadic(G__7291,G__7292,G__7293,G__7294,seq7290__$4);
});

cljs.core$macros._EQ__EQ_.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros._EQ__EQ_.cljs$lang$macro = true;

var ret__18490__auto___7301 = cljs.core$macros.dec = (function cljs$core$macros$dec(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-","cljs.core$macros/-",13526976,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,(1))], 0))));
});
cljs.core$macros.dec.cljs$lang$macro = true;

var ret__18490__auto___7302 = cljs.core$macros.inc = (function cljs$core$macros$inc(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","+","cljs.core$macros/+",-18260342,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,(1))], 0))));
});
cljs.core$macros.inc.cljs$lang$macro = true;

var ret__18490__auto___7303 = cljs.core$macros.zero_QMARK_ = (function cljs$core$macros$zero_QMARK_(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","==","cljs.core$macros/==",-818551413,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,(0))], 0))));
});
cljs.core$macros.zero_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7304 = cljs.core$macros.pos_QMARK_ = (function cljs$core$macros$pos_QMARK_(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros",">","cljs.core$macros/>",1703297853,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,(0))], 0))));
});
cljs.core$macros.pos_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7305 = cljs.core$macros.neg_QMARK_ = (function cljs$core$macros$neg_QMARK_(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","<","cljs.core$macros/<",371512596,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,(0))], 0))));
});
cljs.core$macros.neg_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7315 = (function (){
cljs.core$macros.max = (function cljs$core$macros$max(var_args){
var G__7314 = arguments.length;
switch (G__7314) {
case 3:
return cljs.core$macros.max.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros.max.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7317 = arguments.length;
var i__18451__auto___7318 = (0);
while(true){
if((i__18451__auto___7318 < len__18450__auto___7317)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7318]));

var G__7319 = (i__18451__auto___7318 + (1));
i__18451__auto___7318 = G__7319;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros.max.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros.max.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});

cljs.core$macros.max.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7306__auto__","x__7306__auto__",-679530280,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"y__7307__auto__","y__7307__auto__",-1725492214,null)),(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"js*","js*",-1134233646,null)),cljs.core._conj(cljs.core.List.EMPTY,"((~{} > ~{}) ? ~{} : ~{})"),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7306__auto__","x__7306__auto__",-679530280,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"y__7307__auto__","y__7307__auto__",-1725492214,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7306__auto__","x__7306__auto__",-679530280,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"y__7307__auto__","y__7307__auto__",-1725492214,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.max.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","max","cljs.core$macros/max",1176150699,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","max","cljs.core$macros/max",1176150699,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
});

cljs.core$macros.max.cljs$lang$applyTo = (function (seq7309){
var G__7310 = cljs.core.first(seq7309);
var seq7309__$1 = cljs.core.next(seq7309);
var G__7311 = cljs.core.first(seq7309__$1);
var seq7309__$2 = cljs.core.next(seq7309__$1);
var G__7312 = cljs.core.first(seq7309__$2);
var seq7309__$3 = cljs.core.next(seq7309__$2);
var G__7313 = cljs.core.first(seq7309__$3);
var seq7309__$4 = cljs.core.next(seq7309__$3);
return cljs.core$macros.max.cljs$core$IFn$_invoke$arity$variadic(G__7310,G__7311,G__7312,G__7313,seq7309__$4);
});

cljs.core$macros.max.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros.max.cljs$lang$macro = true;

var ret__18490__auto___7329 = (function (){
cljs.core$macros.min = (function cljs$core$macros$min(var_args){
var G__7328 = arguments.length;
switch (G__7328) {
case 3:
return cljs.core$macros.min.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros.min.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7331 = arguments.length;
var i__18451__auto___7332 = (0);
while(true){
if((i__18451__auto___7332 < len__18450__auto___7331)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7332]));

var G__7333 = (i__18451__auto___7332 + (1));
i__18451__auto___7332 = G__7333;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros.min.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros.min.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
return x;
});

cljs.core$macros.min.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7320__auto__","x__7320__auto__",291176285,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"y__7321__auto__","y__7321__auto__",-983054267,null)),(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"js*","js*",-1134233646,null)),cljs.core._conj(cljs.core.List.EMPTY,"((~{} < ~{}) ? ~{} : ~{})"),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7320__auto__","x__7320__auto__",291176285,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"y__7321__auto__","y__7321__auto__",-983054267,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7320__auto__","x__7320__auto__",291176285,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"y__7321__auto__","y__7321__auto__",-983054267,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.min.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","min","cljs.core$macros/min",1499775161,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","min","cljs.core$macros/min",1499775161,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
});

cljs.core$macros.min.cljs$lang$applyTo = (function (seq7323){
var G__7324 = cljs.core.first(seq7323);
var seq7323__$1 = cljs.core.next(seq7323);
var G__7325 = cljs.core.first(seq7323__$1);
var seq7323__$2 = cljs.core.next(seq7323__$1);
var G__7326 = cljs.core.first(seq7323__$2);
var seq7323__$3 = cljs.core.next(seq7323__$2);
var G__7327 = cljs.core.first(seq7323__$3);
var seq7323__$4 = cljs.core.next(seq7323__$3);
return cljs.core$macros.min.cljs$core$IFn$_invoke$arity$variadic(G__7324,G__7325,G__7326,G__7327,seq7323__$4);
});

cljs.core$macros.min.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros.min.cljs$lang$macro = true;

var ret__18490__auto___7334 = cljs.core$macros.js_mod = (function cljs$core$macros$js_mod(_AMPERSAND_form,_AMPERSAND_env,num,div){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = num;
return cljs.core._conj((function (){var x__18265__auto____$1 = div;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} % ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.js_mod.cljs$lang$macro = true;

var ret__18490__auto___7335 = cljs.core$macros.bit_not = (function cljs$core$macros$bit_not(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),"(~ ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.bit_not.cljs$lang$macro = true;

var ret__18490__auto___7343 = (function (){
cljs.core$macros.bit_and = (function cljs$core$macros$bit_and(var_args){
var G__7342 = arguments.length;
switch (G__7342) {
case 4:
return cljs.core$macros.bit_and.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7345 = arguments.length;
var i__18451__auto___7346 = (0);
while(true){
if((i__18451__auto___7346 < len__18450__auto___7345)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7346]));

var G__7347 = (i__18451__auto___7346 + (1));
i__18451__auto___7346 = G__7347;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros.bit_and.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros.bit_and.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} & ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});

cljs.core$macros.bit_and.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","bit-and","cljs.core$macros/bit-and",-1069060797,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","bit-and","cljs.core$macros/bit-and",-1069060797,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
});

cljs.core$macros.bit_and.cljs$lang$applyTo = (function (seq7337){
var G__7338 = cljs.core.first(seq7337);
var seq7337__$1 = cljs.core.next(seq7337);
var G__7339 = cljs.core.first(seq7337__$1);
var seq7337__$2 = cljs.core.next(seq7337__$1);
var G__7340 = cljs.core.first(seq7337__$2);
var seq7337__$3 = cljs.core.next(seq7337__$2);
var G__7341 = cljs.core.first(seq7337__$3);
var seq7337__$4 = cljs.core.next(seq7337__$3);
return cljs.core$macros.bit_and.cljs$core$IFn$_invoke$arity$variadic(G__7338,G__7339,G__7340,G__7341,seq7337__$4);
});

cljs.core$macros.bit_and.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros.bit_and.cljs$lang$macro = true;

var ret__18490__auto___7355 = (function (){
cljs.core$macros.unsafe_bit_and = (function cljs$core$macros$unsafe_bit_and(var_args){
var G__7354 = arguments.length;
switch (G__7354) {
case 4:
return cljs.core$macros.unsafe_bit_and.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7357 = arguments.length;
var i__18451__auto___7358 = (0);
while(true){
if((i__18451__auto___7358 < len__18450__auto___7357)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7358]));

var G__7359 = (i__18451__auto___7358 + (1));
i__18451__auto___7358 = G__7359;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros.unsafe_bit_and.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros.unsafe_bit_and.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} & ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});

cljs.core$macros.unsafe_bit_and.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unsafe-bit-and","cljs.core$macros/unsafe-bit-and",1803731600,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unsafe-bit-and","cljs.core$macros/unsafe-bit-and",1803731600,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
});

cljs.core$macros.unsafe_bit_and.cljs$lang$applyTo = (function (seq7349){
var G__7350 = cljs.core.first(seq7349);
var seq7349__$1 = cljs.core.next(seq7349);
var G__7351 = cljs.core.first(seq7349__$1);
var seq7349__$2 = cljs.core.next(seq7349__$1);
var G__7352 = cljs.core.first(seq7349__$2);
var seq7349__$3 = cljs.core.next(seq7349__$2);
var G__7353 = cljs.core.first(seq7349__$3);
var seq7349__$4 = cljs.core.next(seq7349__$3);
return cljs.core$macros.unsafe_bit_and.cljs$core$IFn$_invoke$arity$variadic(G__7350,G__7351,G__7352,G__7353,seq7349__$4);
});

cljs.core$macros.unsafe_bit_and.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros.unsafe_bit_and.cljs$lang$macro = true;

var ret__18490__auto___7367 = (function (){
cljs.core$macros.bit_or = (function cljs$core$macros$bit_or(var_args){
var G__7366 = arguments.length;
switch (G__7366) {
case 4:
return cljs.core$macros.bit_or.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7369 = arguments.length;
var i__18451__auto___7370 = (0);
while(true){
if((i__18451__auto___7370 < len__18450__auto___7369)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7370]));

var G__7371 = (i__18451__auto___7370 + (1));
i__18451__auto___7370 = G__7371;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros.bit_or.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros.bit_or.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} | ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});

cljs.core$macros.bit_or.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","bit-or","cljs.core$macros/bit-or",1463236165,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","bit-or","cljs.core$macros/bit-or",1463236165,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
});

cljs.core$macros.bit_or.cljs$lang$applyTo = (function (seq7361){
var G__7362 = cljs.core.first(seq7361);
var seq7361__$1 = cljs.core.next(seq7361);
var G__7363 = cljs.core.first(seq7361__$1);
var seq7361__$2 = cljs.core.next(seq7361__$1);
var G__7364 = cljs.core.first(seq7361__$2);
var seq7361__$3 = cljs.core.next(seq7361__$2);
var G__7365 = cljs.core.first(seq7361__$3);
var seq7361__$4 = cljs.core.next(seq7361__$3);
return cljs.core$macros.bit_or.cljs$core$IFn$_invoke$arity$variadic(G__7362,G__7363,G__7364,G__7365,seq7361__$4);
});

cljs.core$macros.bit_or.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros.bit_or.cljs$lang$macro = true;

var ret__18490__auto___7372 = cljs.core$macros.int$ = (function cljs$core$macros$int(_AMPERSAND_form,_AMPERSAND_env,x){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","bit-or","cljs.core$macros/bit-or",1463236165,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,(0))], 0))));
});
cljs.core$macros.int$.cljs$lang$macro = true;

var ret__18490__auto___7380 = (function (){
cljs.core$macros.bit_xor = (function cljs$core$macros$bit_xor(var_args){
var G__7379 = arguments.length;
switch (G__7379) {
case 4:
return cljs.core$macros.bit_xor.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7382 = arguments.length;
var i__18451__auto___7383 = (0);
while(true){
if((i__18451__auto___7383 < len__18450__auto___7382)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7383]));

var G__7384 = (i__18451__auto___7383 + (1));
i__18451__auto___7383 = G__7384;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros.bit_xor.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros.bit_xor.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} ^ ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});

cljs.core$macros.bit_xor.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","bit-xor","cljs.core$macros/bit-xor",1284619191,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","bit-xor","cljs.core$macros/bit-xor",1284619191,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
});

cljs.core$macros.bit_xor.cljs$lang$applyTo = (function (seq7374){
var G__7375 = cljs.core.first(seq7374);
var seq7374__$1 = cljs.core.next(seq7374);
var G__7376 = cljs.core.first(seq7374__$1);
var seq7374__$2 = cljs.core.next(seq7374__$1);
var G__7377 = cljs.core.first(seq7374__$2);
var seq7374__$3 = cljs.core.next(seq7374__$2);
var G__7378 = cljs.core.first(seq7374__$3);
var seq7374__$4 = cljs.core.next(seq7374__$3);
return cljs.core$macros.bit_xor.cljs$core$IFn$_invoke$arity$variadic(G__7375,G__7376,G__7377,G__7378,seq7374__$4);
});

cljs.core$macros.bit_xor.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros.bit_xor.cljs$lang$macro = true;

var ret__18490__auto___7392 = (function (){
cljs.core$macros.bit_and_not = (function cljs$core$macros$bit_and_not(var_args){
var G__7391 = arguments.length;
switch (G__7391) {
case 4:
return cljs.core$macros.bit_and_not.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7394 = arguments.length;
var i__18451__auto___7395 = (0);
while(true){
if((i__18451__auto___7395 < len__18450__auto___7394)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7395]));

var G__7396 = (i__18451__auto___7395 + (1));
i__18451__auto___7395 = G__7396;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros.bit_and_not.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros.bit_and_not.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,y){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} & ~~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});

cljs.core$macros.bit_and_not.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,y,more){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","bit-and-not","cljs.core$macros/bit-and-not",-537076037,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","bit-and-not","cljs.core$macros/bit-and-not",-537076037,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = y;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([more], 0))));
});

cljs.core$macros.bit_and_not.cljs$lang$applyTo = (function (seq7386){
var G__7387 = cljs.core.first(seq7386);
var seq7386__$1 = cljs.core.next(seq7386);
var G__7388 = cljs.core.first(seq7386__$1);
var seq7386__$2 = cljs.core.next(seq7386__$1);
var G__7389 = cljs.core.first(seq7386__$2);
var seq7386__$3 = cljs.core.next(seq7386__$2);
var G__7390 = cljs.core.first(seq7386__$3);
var seq7386__$4 = cljs.core.next(seq7386__$3);
return cljs.core$macros.bit_and_not.cljs$core$IFn$_invoke$arity$variadic(G__7387,G__7388,G__7389,G__7390,seq7386__$4);
});

cljs.core$macros.bit_and_not.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros.bit_and_not.cljs$lang$macro = true;

var ret__18490__auto___7397 = cljs.core$macros.bit_clear = (function cljs$core$macros$bit_clear(_AMPERSAND_form,_AMPERSAND_env,x,n){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = n;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} & ~(1 << ~{}))"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.bit_clear.cljs$lang$macro = true;

var ret__18490__auto___7398 = cljs.core$macros.bit_flip = (function cljs$core$macros$bit_flip(_AMPERSAND_form,_AMPERSAND_env,x,n){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = n;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} ^ (1 << ~{}))"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.bit_flip.cljs$lang$macro = true;

var ret__18490__auto___7399 = cljs.core$macros.bit_test = (function cljs$core$macros$bit_test(_AMPERSAND_form,_AMPERSAND_env,x,n){
return cljs.core$macros.bool_expr(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = n;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"((~{} & (1 << ~{})) != 0)"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)));
});
cljs.core$macros.bit_test.cljs$lang$macro = true;

var ret__18490__auto___7400 = cljs.core$macros.bit_shift_left = (function cljs$core$macros$bit_shift_left(_AMPERSAND_form,_AMPERSAND_env,x,n){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = n;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} << ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.bit_shift_left.cljs$lang$macro = true;

var ret__18490__auto___7401 = cljs.core$macros.bit_shift_right = (function cljs$core$macros$bit_shift_right(_AMPERSAND_form,_AMPERSAND_env,x,n){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = n;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} >> ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.bit_shift_right.cljs$lang$macro = true;

var ret__18490__auto___7402 = cljs.core$macros.bit_shift_right_zero_fill = (function cljs$core$macros$bit_shift_right_zero_fill(_AMPERSAND_form,_AMPERSAND_env,x,n){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = n;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} >>> ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.bit_shift_right_zero_fill.cljs$lang$macro = true;

var ret__18490__auto___7403 = cljs.core$macros.unsigned_bit_shift_right = (function cljs$core$macros$unsigned_bit_shift_right(_AMPERSAND_form,_AMPERSAND_env,x,n){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = n;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} >>> ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.unsigned_bit_shift_right.cljs$lang$macro = true;

var ret__18490__auto___7404 = cljs.core$macros.bit_set = (function cljs$core$macros$bit_set(_AMPERSAND_form,_AMPERSAND_env,x,n){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = x;
return cljs.core._conj((function (){var x__18265__auto____$1 = n;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"(~{} | (1 << ~{}))"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.bit_set.cljs$lang$macro = true;

var ret__18490__auto___7405 = cljs.core$macros.mask = (function cljs$core$macros$mask(_AMPERSAND_form,_AMPERSAND_env,hash,shift){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = hash;
return cljs.core._conj((function (){var x__18265__auto____$1 = shift;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),"((~{} >>> ~{}) & 0x01f)"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.mask.cljs$lang$macro = true;

var ret__18490__auto___7406 = cljs.core$macros.bitpos = (function cljs$core$macros$bitpos(_AMPERSAND_form,_AMPERSAND_env,hash,shift){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","mask","cljs.core$macros/mask",1575319768,null)),(function (){var x__18265__auto__ = hash;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = shift;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),"(1 << ~{})"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.bitpos.cljs$lang$macro = true;

var ret__18490__auto___7408 = cljs.core$macros.caching_hash = (function cljs$core$macros$caching_hash(_AMPERSAND_form,_AMPERSAND_env,coll,hash_fn,hash_key){
if((hash_key instanceof cljs.core.Symbol)){
} else {
throw (new Error(["Assert failed: ","hash-key is substituted twice","\n","(clojure.core/symbol? hash-key)"].join('')));
}

return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"h__7407__auto__","h__7407__auto__",371998133,null)),(function (){var x__18265__auto__ = hash_key;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","if-not","cljs.core$macros/if-not",-1825285737,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"h__7407__auto__","h__7407__auto__",371998133,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"h__7407__auto__","h__7407__auto__",371998133,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"h__7407__auto__","h__7407__auto__",371998133,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = hash_fn;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = coll;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = hash_key;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"h__7407__auto__","h__7407__auto__",371998133,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"h__7407__auto__","h__7407__auto__",371998133,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.caching_hash.cljs$lang$macro = true;

cljs.core$macros.do_curried = (function cljs$core$macros$do_curried(name,doc,meta,args,body){
var cargs = cljs.core.vec(cljs.core.butlast(args));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","defn","cljs.core$macros/defn",-728332354,null)),(function (){var x__18265__auto__ = name;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = doc;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = meta;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cargs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7409__auto__","x__7409__auto__",489057224,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = name;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cargs,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7409__auto__","x__7409__auto__",489057224,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = args;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),body)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
var ret__18490__auto___7417 = (function (){
/**
 * Builds another arity of the fn that returns a fn awaiting the last
 *   param
 */
cljs.core$macros.defcurried = (function cljs$core$macros$defcurried(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7418 = arguments.length;
var i__18451__auto___7419 = (0);
while(true){
if((i__18451__auto___7419 < len__18450__auto___7418)){
args__18453__auto__.push((arguments[i__18451__auto___7419]));

var G__7420 = (i__18451__auto___7419 + (1));
i__18451__auto___7419 = G__7420;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((6) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((6)),(0),null)):null);
return cljs.core$macros.defcurried.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),(arguments[(5)]),argseq__18454__auto__);
});

cljs.core$macros.defcurried.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,name,doc,meta,args,body){
return cljs.core$macros.do_curried(name,doc,meta,args,body);
});

cljs.core$macros.defcurried.cljs$lang$maxFixedArity = (6);

cljs.core$macros.defcurried.cljs$lang$applyTo = (function (seq7410){
var G__7411 = cljs.core.first(seq7410);
var seq7410__$1 = cljs.core.next(seq7410);
var G__7412 = cljs.core.first(seq7410__$1);
var seq7410__$2 = cljs.core.next(seq7410__$1);
var G__7413 = cljs.core.first(seq7410__$2);
var seq7410__$3 = cljs.core.next(seq7410__$2);
var G__7414 = cljs.core.first(seq7410__$3);
var seq7410__$4 = cljs.core.next(seq7410__$3);
var G__7415 = cljs.core.first(seq7410__$4);
var seq7410__$5 = cljs.core.next(seq7410__$4);
var G__7416 = cljs.core.first(seq7410__$5);
var seq7410__$6 = cljs.core.next(seq7410__$5);
return cljs.core$macros.defcurried.cljs$core$IFn$_invoke$arity$variadic(G__7411,G__7412,G__7413,G__7414,G__7415,G__7416,seq7410__$6);
});

return null;
})()
;
cljs.core$macros.defcurried.cljs$lang$macro = true;

cljs.core$macros.do_rfn = (function cljs$core$macros$do_rfn(f1,k,fkv){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1((function (){var x__18265__auto__ = f1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = clojure.walk.postwalk((function (p1__7421_SHARP_){
if(cljs.core.sequential_QMARK_(p1__7421_SHARP_)){
var G__7425 = cljs.core.remove.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentHashSet.createAsIfByAssoc([k]),p1__7421_SHARP_);
var fexpr__7424 = ((cljs.core.vector_QMARK_(p1__7421_SHARP_))?cljs.core.vec:cljs.core.identity);
return (fexpr__7424.cljs$core$IFn$_invoke$arity$1 ? fexpr__7424.cljs$core$IFn$_invoke$arity$1(G__7425) : fexpr__7424.call(null,G__7425));
} else {
return p1__7421_SHARP_;
}
}),fkv);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = fkv;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
var ret__18490__auto___7430 = /**
 * Builds 3-arity reducing fn given names of wrapped fn and key, and k/v impl.
 */
cljs.core$macros.rfn = (function cljs$core$macros$rfn(_AMPERSAND_form,_AMPERSAND_env,p__7426,fkv){
var vec__7427 = p__7426;
var f1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7427,(0),null);
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7427,(1),null);
return cljs.core$macros.do_rfn(f1,k,fkv);
});
cljs.core$macros.rfn.cljs$lang$macro = true;

cljs.core$macros.protocol_prefix = (function cljs$core$macros$protocol_prefix(psym){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1([cljs.core.str.cljs$core$IFn$_invoke$arity$1(psym)].join('').replace((new RegExp("\\.","g")),"$").replace("/","$")),"$"].join('');
});
cljs.core$macros.base_type = new cljs.core.PersistentArrayMap(null, 8, [null,"null",new cljs.core.Symbol(null,"object","object",-1179821820,null),"object",new cljs.core.Symbol(null,"string","string",-349010059,null),"string",new cljs.core.Symbol(null,"number","number",-1084057331,null),"number",new cljs.core.Symbol(null,"array","array",-440182315,null),"array",new cljs.core.Symbol(null,"function","function",-486723946,null),"function",new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),"boolean",new cljs.core.Symbol(null,"default","default",-347290801,null),"_"], null);
cljs.core$macros.js_base_type = new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Symbol("js","Boolean","js/Boolean",1661145260,null),"boolean",new cljs.core.Symbol("js","String","js/String",-2070054036,null),"string",new cljs.core.Symbol("js","Array","js/Array",-423508366,null),"array",new cljs.core.Symbol("js","Object","js/Object",61215323,null),"object",new cljs.core.Symbol("js","Number","js/Number",-508133572,null),"number",new cljs.core.Symbol("js","Function","js/Function",-749892063,null),"function"], null);
var ret__18490__auto___7434 = (function (){
/**
 * reify is a macro with the following structure:
 * 
 *  (reify options* specs*)
 * 
 *   Currently there are no options.
 * 
 *   Each spec consists of the protocol name followed by zero
 *   or more method bodies:
 * 
 *   protocol
 *   (methodName [args+] body)*
 * 
 *   Methods should be supplied for all methods of the desired
 *   protocol(s). You can also define overrides for Object methods. Note that
 *   the first parameter must be supplied to correspond to the target object
 *   ('this' in JavaScript parlance). Note also that recur calls
 *   to the method head should *not* pass the target object, it will be supplied
 *   automatically and can not be substituted.
 * 
 *   recur works to method heads The method bodies of reify are lexical
 *   closures, and can refer to the surrounding local scope:
 * 
 *   (str (let [f "foo"]
 *     (reify Object
 *       (toString [this] f))))
 *   == "foo"
 * 
 *   (seq (let [f "foo"]
 *     (reify ISeqable
 *       (-seq [this] (seq f)))))
 *   == ("f" "o" "o"))
 * 
 *   reify always implements IMeta and IWithMeta and transfers meta
 *   data of the form to the created object.
 * 
 *   (meta ^{:k :v} (reify Object (toString [this] "foo")))
 *   == {:k :v}
 */
cljs.core$macros.reify = (function cljs$core$macros$reify(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7435 = arguments.length;
var i__18451__auto___7436 = (0);
while(true){
if((i__18451__auto___7436 < len__18450__auto___7435)){
args__18453__auto__.push((arguments[i__18451__auto___7436]));

var G__7437 = (i__18451__auto___7436 + (1));
i__18451__auto___7436 = G__7437;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.reify.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.reify.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,impls){
var t = cljs.core.with_meta(cljs.core.gensym.cljs$core$IFn$_invoke$arity$1(["t_",cljs.core.str.cljs$core$IFn$_invoke$arity$1(clojure.string.replace([cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.munge(cljs.analyzer._STAR_cljs_ns_STAR_))].join(''),".","$"))].join('')),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"anonymous","anonymous",447897231),true], null));
var meta_sym = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("meta");
var this_sym = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("_");
var locals = cljs.core.keys(new cljs.core.Keyword(null,"locals","locals",535295783).cljs$core$IFn$_invoke$arity$1(_AMPERSAND_env));
var ns = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(_AMPERSAND_env));
var munge = cljs.compiler.munge;
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when-not","cljs.core$macros/when-not",-764302244,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","exists?","cljs.core$macros/exists?",-1828590389,null)),(function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$2([cljs.core.str.cljs$core$IFn$_invoke$arity$1(ns)].join(''),[cljs.core.str.cljs$core$IFn$_invoke$arity$1(t)].join(''));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","deftype","cljs.core$macros/deftype",1799045688,null)),(function (){var x__18265__auto__ = t;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(locals,(function (){var x__18265__auto__ = meta_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","IWithMeta","cljs.core$macros/IWithMeta",-21265910,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-with-meta","-with-meta",-1610713823,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = this_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = meta_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"new","new",-444906321,null)),(function (){var x__18265__auto__ = t;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([locals,(function (){var x__18265__auto__ = meta_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","IMeta","cljs.core$macros/IMeta",1549760344,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-meta","-meta",494863156,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1((function (){var x__18265__auto__ = this_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = meta_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),impls], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"new","new",-444906321,null)),(function (){var x__18265__auto__ = t;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([locals,(function (){var x__18265__auto__ = cljs.analyzer.elide_reader_meta(cljs.core.meta(_AMPERSAND_form));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.reify.cljs$lang$maxFixedArity = (2);

cljs.core$macros.reify.cljs$lang$applyTo = (function (seq7431){
var G__7432 = cljs.core.first(seq7431);
var seq7431__$1 = cljs.core.next(seq7431);
var G__7433 = cljs.core.first(seq7431__$1);
var seq7431__$2 = cljs.core.next(seq7431__$1);
return cljs.core$macros.reify.cljs$core$IFn$_invoke$arity$variadic(G__7432,G__7433,seq7431__$2);
});

return null;
})()
;
cljs.core$macros.reify.cljs$lang$macro = true;

var ret__18490__auto___7442 = (function (){
/**
 * Identical to reify but mutates its first argument.
 */
cljs.core$macros.specify_BANG_ = (function cljs$core$macros$specify_BANG_(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7443 = arguments.length;
var i__18451__auto___7444 = (0);
while(true){
if((i__18451__auto___7444 < len__18450__auto___7443)){
args__18453__auto__.push((arguments[i__18451__auto___7444]));

var G__7445 = (i__18451__auto___7444 + (1));
i__18451__auto___7444 = G__7445;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.specify_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.specify_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,expr,impls){
var x = cljs.core.with_meta(cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("x"),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"extend","extend",1836484006),new cljs.core.Keyword(null,"instance","instance",-2121349050)], null));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","extend-type","cljs.core$macros/extend-type",1713295201,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([impls], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.specify_BANG_.cljs$lang$maxFixedArity = (3);

cljs.core$macros.specify_BANG_.cljs$lang$applyTo = (function (seq7438){
var G__7439 = cljs.core.first(seq7438);
var seq7438__$1 = cljs.core.next(seq7438);
var G__7440 = cljs.core.first(seq7438__$1);
var seq7438__$2 = cljs.core.next(seq7438__$1);
var G__7441 = cljs.core.first(seq7438__$2);
var seq7438__$3 = cljs.core.next(seq7438__$2);
return cljs.core$macros.specify_BANG_.cljs$core$IFn$_invoke$arity$variadic(G__7439,G__7440,G__7441,seq7438__$3);
});

return null;
})()
;
cljs.core$macros.specify_BANG_.cljs$lang$macro = true;

var ret__18490__auto___7450 = (function (){
/**
 * Identical to specify! but does not mutate its first argument. The first
 *   argument must be an ICloneable instance.
 */
cljs.core$macros.specify = (function cljs$core$macros$specify(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7451 = arguments.length;
var i__18451__auto___7452 = (0);
while(true){
if((i__18451__auto___7452 < len__18450__auto___7451)){
args__18453__auto__.push((arguments[i__18451__auto___7452]));

var G__7453 = (i__18451__auto___7452 + (1));
i__18451__auto___7452 = G__7453;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.specify.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.specify.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,expr,impls){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","specify!","cljs.core/specify!",-585401629,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","clone","cljs.core/clone",1417120092,null)),(function (){var x__18265__auto__ = expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([impls], 0))));
});

cljs.core$macros.specify.cljs$lang$maxFixedArity = (3);

cljs.core$macros.specify.cljs$lang$applyTo = (function (seq7446){
var G__7447 = cljs.core.first(seq7446);
var seq7446__$1 = cljs.core.next(seq7446);
var G__7448 = cljs.core.first(seq7446__$1);
var seq7446__$2 = cljs.core.next(seq7446__$1);
var G__7449 = cljs.core.first(seq7446__$2);
var seq7446__$3 = cljs.core.next(seq7446__$2);
return cljs.core$macros.specify.cljs$core$IFn$_invoke$arity$variadic(G__7447,G__7448,G__7449,seq7446__$3);
});

return null;
})()
;
cljs.core$macros.specify.cljs$lang$macro = true;

var ret__18490__auto___7454 = cljs.core$macros.js_this = (function cljs$core$macros$js_this(_AMPERSAND_form,_AMPERSAND_env){
return cljs.core._conj(cljs.core._conj(cljs.core.List.EMPTY,"this"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.js_this.cljs$lang$macro = true;

var ret__18490__auto___7459 = (function (){
/**
 * Defines a scope where JavaScript's implicit "this" is bound to the name provided.
 */
cljs.core$macros.this_as = (function cljs$core$macros$this_as(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7460 = arguments.length;
var i__18451__auto___7461 = (0);
while(true){
if((i__18451__auto___7461 < len__18450__auto___7460)){
args__18453__auto__.push((arguments[i__18451__auto___7461]));

var G__7462 = (i__18451__auto___7461 + (1));
i__18451__auto___7461 = G__7462;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.this_as.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.this_as.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,name,body){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = name;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","js-this","cljs.core$macros/js-this",353597180,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
});

cljs.core$macros.this_as.cljs$lang$maxFixedArity = (3);

cljs.core$macros.this_as.cljs$lang$applyTo = (function (seq7455){
var G__7456 = cljs.core.first(seq7455);
var seq7455__$1 = cljs.core.next(seq7455);
var G__7457 = cljs.core.first(seq7455__$1);
var seq7455__$2 = cljs.core.next(seq7455__$1);
var G__7458 = cljs.core.first(seq7455__$2);
var seq7455__$3 = cljs.core.next(seq7455__$2);
return cljs.core$macros.this_as.cljs$core$IFn$_invoke$arity$variadic(G__7456,G__7457,G__7458,seq7455__$3);
});

return null;
})()
;
cljs.core$macros.this_as.cljs$lang$macro = true;

cljs.core$macros.to_property = (function cljs$core$macros$to_property(sym){
return cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["-",cljs.core.str.cljs$core$IFn$_invoke$arity$1(sym)].join(''));
});
cljs.core$macros.warn_and_update_protocol = (function cljs$core$macros$warn_and_update_protocol(p,type,env){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Symbol(null,"Object","Object",61210754,null),p)){
return null;
} else {
var temp__5457__auto__ = cljs.analyzer.resolve_existing_var(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(env,new cljs.core.Keyword(null,"locals","locals",535295783)),p);
if(cljs.core.truth_(temp__5457__auto__)){
var var$ = temp__5457__auto__;
if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol-symbol","protocol-symbol",1279552198).cljs$core$IFn$_invoke$arity$1(var$))){
} else {
cljs.analyzer.warning(new cljs.core.Keyword(null,"invalid-protocol-symbol","invalid-protocol-symbol",86246948),env,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"protocol","protocol",652470118),p], null));
}

if(cljs.core.truth_((function (){var and__17759__auto__ = new cljs.core.Keyword(null,"protocol-deprecated","protocol-deprecated",103233497).cljs$core$IFn$_invoke$arity$1(cljs.analyzer._STAR_cljs_warnings_STAR_);
if(cljs.core.truth_(and__17759__auto__)){
var and__17759__auto____$1 = new cljs.core.Keyword(null,"deprecated","deprecated",1498275348).cljs$core$IFn$_invoke$arity$1(var$);
if(cljs.core.truth_(and__17759__auto____$1)){
return cljs.core.not(new cljs.core.Keyword(null,"deprecation-nowarn","deprecation-nowarn",-1762828044).cljs$core$IFn$_invoke$arity$1(cljs.core.meta(p)));
} else {
return and__17759__auto____$1;
}
} else {
return and__17759__auto__;
}
})())){
cljs.analyzer.warning(new cljs.core.Keyword(null,"protocol-deprecated","protocol-deprecated",103233497),env,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"protocol","protocol",652470118),p], null));
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol-symbol","protocol-symbol",1279552198).cljs$core$IFn$_invoke$arity$1(var$))){
return cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$4(cljs.env._STAR_compiler_STAR_,cljs.core.update_in,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("cljs.analyzer","namespaces","cljs.analyzer/namespaces",-260788927)], null),((function (var$,temp__5457__auto__){
return (function (ns){
return cljs.core.update_in.cljs$core$IFn$_invoke$arity$4(ns,new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(var$),new cljs.core.Keyword(null,"defs","defs",1398449717),cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(cljs.core.name(p)),new cljs.core.Keyword(null,"impls","impls",-1314014853)], null),cljs.core.conj,type);
});})(var$,temp__5457__auto__))
);
} else {
return null;
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"undeclared","undeclared",1446667347).cljs$core$IFn$_invoke$arity$1(cljs.analyzer._STAR_cljs_warnings_STAR_))){
return cljs.analyzer.warning(new cljs.core.Keyword(null,"undeclared-protocol-symbol","undeclared-protocol-symbol",462882867),env,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"protocol","protocol",652470118),p], null));
} else {
return null;
}
}
}
});
cljs.core$macros.resolve_var = (function cljs$core$macros$resolve_var(env,sym){
var ret = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(cljs.analyzer.resolve_var.cljs$core$IFn$_invoke$arity$2(env,sym));
if(cljs.core.truth_(ret)){
} else {
throw (new Error(["Assert failed: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(["Can't resolve: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(sym)].join('')),"\n","ret"].join('')));
}

return ret;
});
cljs.core$macros.__GT_impl_map = (function cljs$core$macros$__GT_impl_map(impls){
var ret = cljs.core.PersistentArrayMap.EMPTY;
var s = impls;
while(true){
if(cljs.core.seq(s)){
var G__7463 = cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(ret,cljs.core.first(s),cljs.core.take_while.cljs$core$IFn$_invoke$arity$2(cljs.core.seq_QMARK_,cljs.core.next(s)));
var G__7464 = cljs.core.drop_while.cljs$core$IFn$_invoke$arity$2(cljs.core.seq_QMARK_,cljs.core.next(s));
ret = G__7463;
s = G__7464;
continue;
} else {
return ret;
}
break;
}
});
cljs.core$macros.base_assign_impls = (function cljs$core$macros$base_assign_impls(env,resolve,tsym,type,p__7465){
var vec__7466 = p__7465;
var p = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7466,(0),null);
var sigs = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7466,(1),null);
cljs.core$macros.warn_and_update_protocol(p,tsym,env);

var psym = (resolve.cljs$core$IFn$_invoke$arity$1 ? resolve.cljs$core$IFn$_invoke$arity$1(p) : resolve.call(null,p));
var pfn_prefix = cljs.core.subs.cljs$core$IFn$_invoke$arity$3([cljs.core.str.cljs$core$IFn$_invoke$arity$1(psym)].join(''),(0),([cljs.core.str.cljs$core$IFn$_invoke$arity$1(psym)].join('').indexOf("/") + (1)));
return cljs.core.cons(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("goog.object","set","goog.object/set",-2123361230,null)),(function (){var x__18265__auto__ = psym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = type;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,true)], 0)))),cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (psym,pfn_prefix,vec__7466,p,sigs){
return (function (p__7469){
var vec__7470 = p__7469;
var seq__7471 = cljs.core.seq(vec__7470);
var first__7472 = cljs.core.first(seq__7471);
var seq__7471__$1 = cljs.core.next(seq__7471);
var f = first__7472;
var meths = seq__7471__$1;
var form = vec__7470;
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("goog.object","set","goog.object/set",-2123361230,null)),(function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1([cljs.core.str.cljs$core$IFn$_invoke$arity$1(pfn_prefix),cljs.core.str.cljs$core$IFn$_invoke$arity$1(f)].join(''));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = type;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.with_meta(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),meths))),cljs.core.meta(form));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});})(psym,pfn_prefix,vec__7466,p,sigs))
,sigs));
});
if(typeof cljs.core$macros.extend_prefix !== 'undefined'){
} else {
cljs.core$macros.extend_prefix = (function (){var method_table__18326__auto__ = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.PersistentArrayMap.EMPTY);
var prefer_table__18327__auto__ = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.PersistentArrayMap.EMPTY);
var method_cache__18328__auto__ = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.PersistentArrayMap.EMPTY);
var cached_hierarchy__18329__auto__ = cljs.core.atom.cljs$core$IFn$_invoke$arity$1(cljs.core.PersistentArrayMap.EMPTY);
var hierarchy__18330__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$3(cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"hierarchy","hierarchy",-1053470341),cljs.core.get_global_hierarchy());
return (new cljs.core.MultiFn(cljs.core.symbol.cljs$core$IFn$_invoke$arity$2("cljs.core$macros","extend-prefix"),((function (method_table__18326__auto__,prefer_table__18327__auto__,method_cache__18328__auto__,cached_hierarchy__18329__auto__,hierarchy__18330__auto__){
return (function (tsym,sym){
return new cljs.core.Keyword(null,"extend","extend",1836484006).cljs$core$IFn$_invoke$arity$1(cljs.core.meta(tsym));
});})(method_table__18326__auto__,prefer_table__18327__auto__,method_cache__18328__auto__,cached_hierarchy__18329__auto__,hierarchy__18330__auto__))
,new cljs.core.Keyword(null,"default","default",-1987822328),hierarchy__18330__auto__,method_table__18326__auto__,prefer_table__18327__auto__,method_cache__18328__auto__,cached_hierarchy__18329__auto__));
})();
}
cljs.core$macros.extend_prefix.cljs$core$IMultiFn$_add_method$arity$3(null,new cljs.core.Keyword(null,"instance","instance",-2121349050),(function (tsym,sym){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"..","..",-300507420,null)),(function (){var x__18265__auto__ = tsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core$macros.to_property(sym);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
}));
cljs.core$macros.extend_prefix.cljs$core$IMultiFn$_add_method$arity$3(null,new cljs.core.Keyword(null,"default","default",-1987822328),(function (tsym,sym){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"..","..",-300507420,null)),(function (){var x__18265__auto__ = tsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-prototype","-prototype",-450831903,null)),(function (){var x__18265__auto__ = cljs.core$macros.to_property(sym);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
}));
cljs.core$macros.adapt_obj_params = (function cljs$core$macros$adapt_obj_params(type,p__7473){
var vec__7474 = p__7473;
var seq__7475 = cljs.core.seq(vec__7474);
var first__7476 = cljs.core.first(seq__7475);
var seq__7475__$1 = cljs.core.next(seq__7475);
var vec__7477 = first__7476;
var seq__7478 = cljs.core.seq(vec__7477);
var first__7479 = cljs.core.first(seq__7478);
var seq__7478__$1 = cljs.core.next(seq__7478);
var this$ = first__7479;
var args = seq__7478__$1;
var sig = vec__7477;
var body = seq__7475__$1;
var x__18265__auto__ = cljs.core.vec(args);
return cljs.core._conj((function (){var x__18265__auto____$1 = cljs.core.list_STAR_.cljs$core$IFn$_invoke$arity$3(new cljs.core.Symbol(null,"this-as","this-as",-848995740,null),cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(this$,cljs.core.assoc,new cljs.core.Keyword(null,"tag","tag",-1290361223),type),body);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
});
cljs.core$macros.adapt_ifn_params = (function cljs$core$macros$adapt_ifn_params(type,p__7480){
var vec__7481 = p__7480;
var seq__7482 = cljs.core.seq(vec__7481);
var first__7483 = cljs.core.first(seq__7482);
var seq__7482__$1 = cljs.core.next(seq__7482);
var vec__7484 = first__7483;
var seq__7485 = cljs.core.seq(vec__7484);
var first__7486 = cljs.core.first(seq__7485);
var seq__7485__$1 = cljs.core.next(seq__7485);
var this$ = first__7486;
var args = seq__7485__$1;
var sig = vec__7484;
var body = seq__7482__$1;
var self_sym = cljs.core.with_meta(new cljs.core.Symbol(null,"self__","self__",-153190816,null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"tag","tag",-1290361223),type], null));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.vec(cljs.core.cons(self_sym,args));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","this-as","cljs.core$macros/this-as",-799075148,null)),(function (){var x__18265__auto__ = self_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = this$;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = self_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});
cljs.core$macros.adapt_ifn_invoke_params = (function cljs$core$macros$adapt_ifn_invoke_params(type,p__7487){
var vec__7488 = p__7487;
var seq__7489 = cljs.core.seq(vec__7488);
var first__7490 = cljs.core.first(seq__7489);
var seq__7489__$1 = cljs.core.next(seq__7489);
var vec__7491 = first__7490;
var seq__7492 = cljs.core.seq(vec__7491);
var first__7493 = cljs.core.first(seq__7492);
var seq__7492__$1 = cljs.core.next(seq__7492);
var this$ = first__7493;
var args = seq__7492__$1;
var sig = vec__7491;
var body = seq__7489__$1;
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.vec(args);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","this-as","cljs.core$macros/this-as",-799075148,null)),(function (){var x__18265__auto__ = cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(this$,cljs.core.assoc,new cljs.core.Keyword(null,"tag","tag",-1290361223),type);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});
cljs.core$macros.adapt_proto_params = (function cljs$core$macros$adapt_proto_params(type,p__7494){
var vec__7495 = p__7494;
var seq__7496 = cljs.core.seq(vec__7495);
var first__7497 = cljs.core.first(seq__7496);
var seq__7496__$1 = cljs.core.next(seq__7496);
var vec__7498 = first__7497;
var seq__7499 = cljs.core.seq(vec__7498);
var first__7500 = cljs.core.first(seq__7499);
var seq__7499__$1 = cljs.core.next(seq__7499);
var this$ = first__7500;
var args = seq__7499__$1;
var sig = vec__7498;
var body = seq__7496__$1;
var this_SINGLEQUOTE_ = cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(this$,cljs.core.assoc,new cljs.core.Keyword(null,"tag","tag",-1290361223),type);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.vec(cljs.core.cons(this_SINGLEQUOTE_,args));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","this-as","cljs.core$macros/this-as",-799075148,null)),(function (){var x__18265__auto__ = this_SINGLEQUOTE_;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});
cljs.core$macros.add_obj_methods = (function cljs$core$macros$add_obj_methods(type,type_sym,sigs){
return cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p__7502){
var vec__7503 = p__7502;
var seq__7504 = cljs.core.seq(vec__7503);
var first__7505 = cljs.core.first(seq__7504);
var seq__7504__$1 = cljs.core.next(seq__7504);
var f = first__7505;
var meths = seq__7504__$1;
var form = vec__7503;
var vec__7506 = ((cljs.core.vector_QMARK_(cljs.core.first(meths)))?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [f,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.rest(form)], null)], null):new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [f,meths], null));
var f__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7506,(0),null);
var meths__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7506,(1),null);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = (cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2 ? cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2(type_sym,f__$1) : cljs.core$macros.extend_prefix.call(null,type_sym,f__$1));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.with_meta(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (vec__7506,f__$1,meths__$1,vec__7503,seq__7504,first__7505,seq__7504__$1,f,meths,form){
return (function (p1__7501_SHARP_){
return cljs.core$macros.adapt_obj_params(type,p1__7501_SHARP_);
});})(vec__7506,f__$1,meths__$1,vec__7503,seq__7504,first__7505,seq__7504__$1,f,meths,form))
,meths__$1)))),cljs.core.meta(form));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
}),sigs);
});
cljs.core$macros.ifn_invoke_methods = (function cljs$core$macros$ifn_invoke_methods(type,type_sym,p__7510){
var vec__7511 = p__7510;
var seq__7512 = cljs.core.seq(vec__7511);
var first__7513 = cljs.core.first(seq__7512);
var seq__7512__$1 = cljs.core.next(seq__7512);
var f = first__7513;
var meths = seq__7512__$1;
var form = vec__7511;
return cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (vec__7511,seq__7512,first__7513,seq__7512__$1,f,meths,form){
return (function (meth){
var arity = cljs.core.count(cljs.core.first(meth));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = (function (){var G__7516 = type_sym;
var G__7517 = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["cljs$core$IFn$_invoke$arity$",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arity)].join(''));
return (cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2 ? cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2(G__7516,G__7517) : cljs.core$macros.extend_prefix.call(null,G__7516,G__7517));
})();
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.with_meta(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = meth;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))),cljs.core.meta(form));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});})(vec__7511,seq__7512,first__7513,seq__7512__$1,f,meths,form))
,cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (vec__7511,seq__7512,first__7513,seq__7512__$1,f,meths,form){
return (function (p1__7509_SHARP_){
return cljs.core$macros.adapt_ifn_invoke_params(type,p1__7509_SHARP_);
});})(vec__7511,seq__7512,first__7513,seq__7512__$1,f,meths,form))
,meths));
});
cljs.core$macros.add_ifn_methods = (function cljs$core$macros$add_ifn_methods(type,type_sym,p__7519){
var vec__7520 = p__7519;
var seq__7521 = cljs.core.seq(vec__7520);
var first__7522 = cljs.core.first(seq__7521);
var seq__7521__$1 = cljs.core.next(seq__7521);
var f = first__7522;
var meths = seq__7521__$1;
var form = vec__7520;
var meths__$1 = cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (vec__7520,seq__7521,first__7522,seq__7521__$1,f,meths,form){
return (function (p1__7518_SHARP_){
return cljs.core$macros.adapt_ifn_params(type,p1__7518_SHARP_);
});})(vec__7520,seq__7521,first__7522,seq__7521__$1,f,meths,form))
,meths);
var this_sym = cljs.core.with_meta(new cljs.core.Symbol(null,"self__","self__",-153190816,null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"tag","tag",-1290361223),type], null));
var argsym = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("args");
return cljs.core.concat.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = (function (){var G__7525 = type_sym;
var G__7526 = new cljs.core.Symbol(null,"call","call",1120531661,null);
return (cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2 ? cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2(G__7525,G__7526) : cljs.core$macros.extend_prefix.call(null,G__7525,G__7526));
})();
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.with_meta(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),meths__$1))),cljs.core.meta(form));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = (function (){var G__7529 = type_sym;
var G__7530 = new cljs.core.Symbol(null,"apply","apply",-1334050276,null);
return (cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2 ? cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2(G__7529,G__7530) : cljs.core$macros.extend_prefix.call(null,G__7529,G__7530));
})();
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.with_meta(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [this_sym,argsym], null);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","this-as","cljs.core$macros/this-as",-799075148,null)),(function (){var x__18265__auto__ = this_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".apply",".apply",-1176201338,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-call",".-call",1760541695,null)),(function (){var x__18265__auto__ = this_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = this_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".concat",".concat",1180408684,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","array","cljs.core$macros/array",49650437,null)),(function (){var x__18265__auto__ = this_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","aclone","cljs.core/aclone",-758078968,null)),(function (){var x__18265__auto__ = argsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),cljs.core.meta(form));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))))], null),cljs.core$macros.ifn_invoke_methods(type,type_sym,form));
});
cljs.core$macros.add_proto_methods_STAR_ = (function cljs$core$macros$add_proto_methods_STAR_(pprefix,type,type_sym,p__7531){
var vec__7532 = p__7531;
var seq__7533 = cljs.core.seq(vec__7532);
var first__7534 = cljs.core.first(seq__7533);
var seq__7533__$1 = cljs.core.next(seq__7533);
var f = first__7534;
var meths = seq__7533__$1;
var form = vec__7532;
var pf = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(pprefix),cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(f))].join('');
if(cljs.core.vector_QMARK_(cljs.core.first(meths))){
var meth = meths;
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = (function (){var G__7537 = type_sym;
var G__7538 = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(pf),"$arity$",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.count(cljs.core.first(meth)))].join('');
return (cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2 ? cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2(G__7537,G__7538) : cljs.core$macros.extend_prefix.call(null,G__7537,G__7538));
})();
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.with_meta(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),cljs.core$macros.adapt_proto_params(type,meth)))),cljs.core.meta(form));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))))], null);
} else {
return cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (pf,vec__7532,seq__7533,first__7534,seq__7533__$1,f,meths,form){
return (function (p__7539){
var vec__7540 = p__7539;
var seq__7541 = cljs.core.seq(vec__7540);
var first__7542 = cljs.core.first(seq__7541);
var seq__7541__$1 = cljs.core.next(seq__7541);
var sig = first__7542;
var body = seq__7541__$1;
var meth = vec__7540;
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = (function (){var G__7545 = type_sym;
var G__7546 = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(pf),"$arity$",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.count(sig))].join('');
return (cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2 ? cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2(G__7545,G__7546) : cljs.core$macros.extend_prefix.call(null,G__7545,G__7546));
})();
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.with_meta(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core$macros.adapt_proto_params(type,meth);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))),cljs.core.meta(form));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});})(pf,vec__7532,seq__7533,first__7534,seq__7533__$1,f,meths,form))
,meths);
}
});
cljs.core$macros.proto_assign_impls = (function cljs$core$macros$proto_assign_impls(env,resolve,type_sym,type,p__7547){
var vec__7548 = p__7547;
var p = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7548,(0),null);
var sigs = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7548,(1),null);
cljs.core$macros.warn_and_update_protocol(p,type,env);

var psym = (resolve.cljs$core$IFn$_invoke$arity$1 ? resolve.cljs$core$IFn$_invoke$arity$1(p) : resolve.call(null,p));
var pprefix = cljs.core$macros.protocol_prefix(psym);
var skip_flag = cljs.core.set(new cljs.core.Keyword(null,"skip-protocol-flag","skip-protocol-flag",-1426798630).cljs$core$IFn$_invoke$arity$1(cljs.core.meta(type_sym)));
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(p,new cljs.core.Symbol(null,"Object","Object",61210754,null))){
return cljs.core$macros.add_obj_methods(type,type_sym,sigs);
} else {
return cljs.core.concat.cljs$core$IFn$_invoke$arity$2((cljs.core.truth_((skip_flag.cljs$core$IFn$_invoke$arity$1 ? skip_flag.cljs$core$IFn$_invoke$arity$1(psym) : skip_flag.call(null,psym)))?null:new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = (cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2 ? cljs.core$macros.extend_prefix.cljs$core$IFn$_invoke$arity$2(type_sym,pprefix) : cljs.core$macros.extend_prefix.call(null,type_sym,pprefix));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","PROTOCOL_SENTINEL","cljs.core/PROTOCOL_SENTINEL",210209696,null))], 0))))], null)),cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic(((function (psym,pprefix,skip_flag,vec__7548,p,sigs){
return (function (sig){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(psym,new cljs.core.Symbol("cljs.core","IFn","cljs.core/IFn",-920223129,null))){
return cljs.core$macros.add_ifn_methods(type,type_sym,sig);
} else {
return cljs.core$macros.add_proto_methods_STAR_(pprefix,type,type_sym,sig);
}
});})(psym,pprefix,skip_flag,vec__7548,p,sigs))
,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([sigs], 0)));
}
});
cljs.core$macros.validate_impl_sigs = (function cljs$core$macros$validate_impl_sigs(env,p,method){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(p,new cljs.core.Symbol(null,"Object","Object",61210754,null))){
return null;
} else {
var var$ = cljs.analyzer.resolve_var.cljs$core$IFn$_invoke$arity$2(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(env,new cljs.core.Keyword(null,"locals","locals",535295783)),p);
var minfo = new cljs.core.Keyword(null,"methods","methods",453930866).cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"protocol-info","protocol-info",1471745843).cljs$core$IFn$_invoke$arity$1(var$));
var method_name = cljs.core.first(method);
var __GT_name = cljs.core.comp.cljs$core$IFn$_invoke$arity$2(cljs.core.symbol,cljs.core.name);
var vec__7551 = ((cljs.core.vector_QMARK_(cljs.core.second(method)))?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(__GT_name.cljs$core$IFn$_invoke$arity$1 ? __GT_name.cljs$core$IFn$_invoke$arity$1(method_name) : __GT_name.call(null,method_name)),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.second(method)], null)], null):new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(__GT_name.cljs$core$IFn$_invoke$arity$1 ? __GT_name.cljs$core$IFn$_invoke$arity$1(method_name) : __GT_name.call(null,method_name)),cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.first,cljs.core.rest(method))], null));
var fname = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7551,(0),null);
var sigs = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7551,(1),null);
var decmeths = cljs.core.get.cljs$core$IFn$_invoke$arity$3(minfo,fname,new cljs.core.Keyword("cljs.core$macros","not-found","cljs.core$macros/not-found",-1226326556));
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(decmeths,new cljs.core.Keyword("cljs.core$macros","not-found","cljs.core$macros/not-found",-1226326556))){
cljs.analyzer.warning(new cljs.core.Keyword(null,"protocol-invalid-method","protocol-invalid-method",522647516),env,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"protocol","protocol",652470118),p,new cljs.core.Keyword(null,"fname","fname",1500291491),fname,new cljs.core.Keyword(null,"no-such-method","no-such-method",1087422840),true], null));
} else {
}

if(cljs.core.truth_(cljs.core.namespace(method_name))){
var method_var_7554 = cljs.analyzer.resolve_var.cljs$core$IFn$_invoke$arity$3(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(env,new cljs.core.Keyword(null,"locals","locals",535295783)),method_name,cljs.analyzer.confirm_var_exist_warning);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(var$),new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(method_var_7554))){
} else {
cljs.analyzer.warning(new cljs.core.Keyword(null,"protocol-invalid-method","protocol-invalid-method",522647516),env,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"protocol","protocol",652470118),p,new cljs.core.Keyword(null,"fname","fname",1500291491),method_name,new cljs.core.Keyword(null,"no-such-method","no-such-method",1087422840),true], null));
}
} else {
}

var sigs__$1 = sigs;
var seen = cljs.core.PersistentHashSet.EMPTY;
while(true){
if(cljs.core.seq(sigs__$1)){
var sig = cljs.core.first(sigs__$1);
var c = cljs.core.count(sig);
if(cljs.core.contains_QMARK_(seen,c)){
cljs.analyzer.warning(new cljs.core.Keyword(null,"protocol-duped-method","protocol-duped-method",15128166),env,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"protocol","protocol",652470118),p,new cljs.core.Keyword(null,"fname","fname",1500291491),fname], null));
} else {
}

if(cljs.core.truth_(cljs.core.some(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Symbol(null,"&","&",-2144855648,null),null], null), null),sig))){
cljs.analyzer.warning(new cljs.core.Keyword(null,"protocol-impl-with-variadic-method","protocol-impl-with-variadic-method",-319321217),env,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"protocol","protocol",652470118),p,new cljs.core.Keyword(null,"name","name",1843675177),fname], null));
} else {
}

if((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(decmeths,new cljs.core.Keyword("cljs.core$macros","not-found","cljs.core$macros/not-found",-1226326556))) && (cljs.core.not(cljs.core.some(cljs.core.PersistentHashSet.createAsIfByAssoc([c]),cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.count,decmeths))))){
cljs.analyzer.warning(new cljs.core.Keyword(null,"protocol-invalid-method","protocol-invalid-method",522647516),env,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"protocol","protocol",652470118),p,new cljs.core.Keyword(null,"fname","fname",1500291491),fname,new cljs.core.Keyword(null,"invalid-arity","invalid-arity",1335461949),c], null));
} else {
}

var G__7555 = cljs.core.next(sigs__$1);
var G__7556 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(seen,c);
sigs__$1 = G__7555;
seen = G__7556;
continue;
} else {
return null;
}
break;
}
}
});
cljs.core$macros.validate_impls = (function cljs$core$macros$validate_impls(env,impls){
var protos = cljs.core.PersistentHashSet.EMPTY;
var impls__$1 = impls;
while(true){
if(cljs.core.seq(impls__$1)){
var proto = cljs.core.first(impls__$1);
var methods$ = cljs.core.take_while.cljs$core$IFn$_invoke$arity$2(cljs.core.seq_QMARK_,cljs.core.next(impls__$1));
var impls__$2 = cljs.core.drop_while.cljs$core$IFn$_invoke$arity$2(cljs.core.seq_QMARK_,cljs.core.next(impls__$1));
if(cljs.core.contains_QMARK_(protos,proto)){
cljs.analyzer.warning(new cljs.core.Keyword(null,"protocol-multiple-impls","protocol-multiple-impls",794179260),env,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"protocol","protocol",652470118),proto], null));
} else {
}

var seen_7560 = cljs.core.PersistentHashSet.EMPTY;
var methods_7561__$1 = methods$;
while(true){
if(cljs.core.seq(methods_7561__$1)){
var vec__7557_7562 = cljs.core.first(methods_7561__$1);
var fname_7563 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7557_7562,(0),null);
var method_7564 = vec__7557_7562;
if(cljs.core.contains_QMARK_(seen_7560,fname_7563)){
cljs.analyzer.warning(new cljs.core.Keyword(null,"extend-type-invalid-method-shape","extend-type-invalid-method-shape",1424103549),env,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"protocol","protocol",652470118),proto,new cljs.core.Keyword(null,"method","method",55703592),fname_7563], null));
} else {
}

cljs.core$macros.validate_impl_sigs(env,proto,method_7564);

var G__7565 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(seen_7560,fname_7563);
var G__7566 = cljs.core.next(methods_7561__$1);
seen_7560 = G__7565;
methods_7561__$1 = G__7566;
continue;
} else {
}
break;
}

var G__7567 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(protos,proto);
var G__7568 = impls__$2;
protos = G__7567;
impls__$1 = G__7568;
continue;
} else {
return null;
}
break;
}
});
cljs.core$macros.type_hint_first_arg = (function cljs$core$macros$type_hint_first_arg(type_sym,argv){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(argv,(0),cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4((argv.cljs$core$IFn$_invoke$arity$1 ? argv.cljs$core$IFn$_invoke$arity$1((0)) : argv.call(null,(0))),cljs.core.assoc,new cljs.core.Keyword(null,"tag","tag",-1290361223),type_sym));
});
cljs.core$macros.type_hint_single_arity_sig = (function cljs$core$macros$type_hint_single_arity_sig(type_sym,sig){
return cljs.core.list_STAR_.cljs$core$IFn$_invoke$arity$3(cljs.core.first(sig),cljs.core$macros.type_hint_first_arg(type_sym,cljs.core.second(sig)),cljs.core.nnext(sig));
});
cljs.core$macros.type_hint_multi_arity_sig = (function cljs$core$macros$type_hint_multi_arity_sig(type_sym,sig){
return cljs.core.list_STAR_.cljs$core$IFn$_invoke$arity$2(cljs.core$macros.type_hint_first_arg(type_sym,cljs.core.first(sig)),cljs.core.next(sig));
});
cljs.core$macros.type_hint_multi_arity_sigs = (function cljs$core$macros$type_hint_multi_arity_sigs(type_sym,sigs){
return cljs.core.list_STAR_.cljs$core$IFn$_invoke$arity$2(cljs.core.first(sigs),cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.partial.cljs$core$IFn$_invoke$arity$2(cljs.core$macros.type_hint_multi_arity_sig,type_sym),cljs.core.rest(sigs)));
});
cljs.core$macros.type_hint_sigs = (function cljs$core$macros$type_hint_sigs(type_sym,sig){
if(cljs.core.vector_QMARK_(cljs.core.second(sig))){
return cljs.core$macros.type_hint_single_arity_sig(type_sym,sig);
} else {
return cljs.core$macros.type_hint_multi_arity_sigs(type_sym,sig);
}
});
cljs.core$macros.type_hint_impl_map = (function cljs$core$macros$type_hint_impl_map(type_sym,impl_map){
return cljs.core.reduce_kv((function (m,proto,sigs){
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(m,proto,cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.partial.cljs$core$IFn$_invoke$arity$2(cljs.core$macros.type_hint_sigs,type_sym),sigs));
}),cljs.core.PersistentArrayMap.EMPTY,impl_map);
});
var ret__18490__auto___7578 = (function (){
/**
 * Extend a type to a series of protocols. Useful when you are
 *   supplying the definitions explicitly inline. Propagates the
 *   type as a type hint on the first argument of all fns.
 * 
 *   type-sym may be
 * 
 * * default, meaning the definitions will apply for any value,
 *   unless an extend-type exists for one of the more specific
 *   cases below.
 * * nil, meaning the definitions will apply for the nil value.
 * * any of object, boolean, number, string, array, or function,
 *   indicating the definitions will apply for values of the
 *   associated base JavaScript types. Note that, for example,
 *   string should be used instead of js/String.
 * * a JavaScript type not covered by the previous list, such
 *   as js/RegExp.
 * * a type defined by deftype or defrecord.
 * 
 *   (extend-type MyType
 *  ICounted
 *  (-count [c] ...)
 *  Foo
 *  (bar [x y] ...)
 *  (baz ([x] ...) ([x y] ...) ...)
 */
cljs.core$macros.extend_type = (function cljs$core$macros$extend_type(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7579 = arguments.length;
var i__18451__auto___7580 = (0);
while(true){
if((i__18451__auto___7580 < len__18450__auto___7579)){
args__18453__auto__.push((arguments[i__18451__auto___7580]));

var G__7581 = (i__18451__auto___7580 + (1));
i__18451__auto___7580 = G__7581;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.extend_type.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.extend_type.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,type_sym,impls){
var env = _AMPERSAND_env;
var _ = cljs.core$macros.validate_impls(env,impls);
var resolve = cljs.core.partial.cljs$core$IFn$_invoke$arity$2(cljs.core$macros.resolve_var,env);
var impl_map = cljs.core$macros.__GT_impl_map(impls);
var impl_map__$1 = (cljs.core.truth_((function (){var fexpr__7577 = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Symbol(null,"boolean","boolean",-278886877,null),null,new cljs.core.Symbol(null,"number","number",-1084057331,null),null], null), null);
return (fexpr__7577.cljs$core$IFn$_invoke$arity$1 ? fexpr__7577.cljs$core$IFn$_invoke$arity$1(type_sym) : fexpr__7577.call(null,type_sym));
})())?cljs.core$macros.type_hint_impl_map(type_sym,impl_map):impl_map);
var vec__7574 = (function (){var temp__5457__auto__ = (cljs.core$macros.base_type.cljs$core$IFn$_invoke$arity$1 ? cljs.core$macros.base_type.cljs$core$IFn$_invoke$arity$1(type_sym) : cljs.core$macros.base_type.call(null,type_sym));
if(cljs.core.truth_(temp__5457__auto__)){
var type = temp__5457__auto__;
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [type,cljs.core$macros.base_assign_impls], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(resolve.cljs$core$IFn$_invoke$arity$1 ? resolve.cljs$core$IFn$_invoke$arity$1(type_sym) : resolve.call(null,type_sym)),cljs.core$macros.proto_assign_impls], null);
}
})();
var type = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7574,(0),null);
var assign_impls = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7574,(1),null);
if(cljs.core.truth_((function (){var and__17759__auto__ = new cljs.core.Keyword(null,"extending-base-js-type","extending-base-js-type",432787264).cljs$core$IFn$_invoke$arity$1(cljs.analyzer._STAR_cljs_warnings_STAR_);
if(cljs.core.truth_(and__17759__auto__)){
return (cljs.core$macros.js_base_type.cljs$core$IFn$_invoke$arity$1 ? cljs.core$macros.js_base_type.cljs$core$IFn$_invoke$arity$1(type_sym) : cljs.core$macros.js_base_type.call(null,type_sym));
} else {
return and__17759__auto__;
}
})())){
cljs.analyzer.warning(new cljs.core.Keyword(null,"extending-base-js-type","extending-base-js-type",432787264),env,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"current-symbol","current-symbol",-932381075),type_sym,new cljs.core.Keyword(null,"suggested-symbol","suggested-symbol",-1329631875),(cljs.core$macros.js_base_type.cljs$core$IFn$_invoke$arity$1 ? cljs.core$macros.js_base_type.cljs$core$IFn$_invoke$arity$1(type_sym) : cljs.core$macros.js_base_type.call(null,type_sym))], null));
} else {
}

return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic(((function (env,_,resolve,impl_map,impl_map__$1,vec__7574,type,assign_impls){
return (function (p1__7569_SHARP_){
return (assign_impls.cljs$core$IFn$_invoke$arity$5 ? assign_impls.cljs$core$IFn$_invoke$arity$5(env,resolve,type_sym,type,p1__7569_SHARP_) : assign_impls.call(null,env,resolve,type_sym,type,p1__7569_SHARP_));
});})(env,_,resolve,impl_map,impl_map__$1,vec__7574,type,assign_impls))
,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([impl_map__$1], 0)))));
});

cljs.core$macros.extend_type.cljs$lang$maxFixedArity = (3);

cljs.core$macros.extend_type.cljs$lang$applyTo = (function (seq7570){
var G__7571 = cljs.core.first(seq7570);
var seq7570__$1 = cljs.core.next(seq7570);
var G__7572 = cljs.core.first(seq7570__$1);
var seq7570__$2 = cljs.core.next(seq7570__$1);
var G__7573 = cljs.core.first(seq7570__$2);
var seq7570__$3 = cljs.core.next(seq7570__$2);
return cljs.core$macros.extend_type.cljs$core$IFn$_invoke$arity$variadic(G__7571,G__7572,G__7573,seq7570__$3);
});

return null;
})()
;
cljs.core$macros.extend_type.cljs$lang$macro = true;

cljs.core$macros.prepare_protocol_masks = (function cljs$core$macros$prepare_protocol_masks(env,impls){
var resolve = cljs.core.partial.cljs$core$IFn$_invoke$arity$2(cljs.core$macros.resolve_var,env);
var impl_map = cljs.core$macros.__GT_impl_map(impls);
var fpp_pbs = cljs.core.seq(cljs.core.keep.cljs$core$IFn$_invoke$arity$2(cljs.core$macros.fast_path_protocols,cljs.core.map.cljs$core$IFn$_invoke$arity$2(resolve,cljs.core.keys(impl_map))));
if(fpp_pbs){
var fpps = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentHashSet.EMPTY,cljs.core.filter.cljs$core$IFn$_invoke$arity$2(cljs.core.partial.cljs$core$IFn$_invoke$arity$2(cljs.core.contains_QMARK_,cljs.core$macros.fast_path_protocols),cljs.core.map.cljs$core$IFn$_invoke$arity$2(resolve,cljs.core.keys(impl_map))));
var parts = (function (){var parts = cljs.core.group_by(cljs.core.first,fpp_pbs);
var parts__$1 = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.juxt.cljs$core$IFn$_invoke$arity$2(cljs.core.key,cljs.core.comp.cljs$core$IFn$_invoke$arity$2(cljs.core.partial.cljs$core$IFn$_invoke$arity$2(cljs.core.map,cljs.core.peek),cljs.core.val)),parts));
return cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.juxt.cljs$core$IFn$_invoke$arity$2(cljs.core.key,cljs.core.comp.cljs$core$IFn$_invoke$arity$2(cljs.core.partial.cljs$core$IFn$_invoke$arity$2(cljs.core.reduce,cljs.core.bit_or),cljs.core.val)),parts__$1));
})();
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [fpps,cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(((function (fpps,parts,resolve,impl_map,fpp_pbs){
return (function (ps,p){
return cljs.core.update_in.cljs$core$IFn$_invoke$arity$3(ps,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [p], null),cljs.core.fnil.cljs$core$IFn$_invoke$arity$2(cljs.core.identity,(0)));
});})(fpps,parts,resolve,impl_map,fpp_pbs))
,parts,cljs.core.range.cljs$core$IFn$_invoke$arity$1(cljs.core$macros.fast_path_protocol_partitions_count))], null);
} else {
return null;
}
});
cljs.core$macros.annotate_specs = (function cljs$core$macros$annotate_specs(annots,v,p__7583){
var vec__7584 = p__7583;
var f = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7584,(0),null);
var sigs = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7584,(1),null);
return cljs.core.conj.cljs$core$IFn$_invoke$arity$2(v,cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$3(cljs.core.cons(f,cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (vec__7584,f,sigs){
return (function (p1__7582_SHARP_){
return cljs.core.cons(cljs.core.second(p1__7582_SHARP_),cljs.core.nnext(p1__7582_SHARP_));
});})(vec__7584,f,sigs))
,sigs)),cljs.core.merge,annots));
});
cljs.core$macros.dt__GT_et = (function cljs$core$macros$dt__GT_et(var_args){
var G__7588 = arguments.length;
switch (G__7588) {
case 3:
return cljs.core$macros.dt__GT_et.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros.dt__GT_et.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core$macros.dt__GT_et.cljs$core$IFn$_invoke$arity$3 = (function (type,specs,fields){
return cljs.core$macros.dt__GT_et.cljs$core$IFn$_invoke$arity$4(type,specs,fields,false);
});

cljs.core$macros.dt__GT_et.cljs$core$IFn$_invoke$arity$4 = (function (type,specs,fields,inline){
var annots = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword("cljs.analyzer","type","cljs.analyzer/type",478749742),type,new cljs.core.Keyword("cljs.analyzer","protocol-impl","cljs.analyzer/protocol-impl",-1523935409),true,new cljs.core.Keyword("cljs.analyzer","protocol-inline","cljs.analyzer/protocol-inline",-1611519026),inline], null);
var ret = cljs.core.PersistentVector.EMPTY;
var specs__$1 = specs;
while(true){
if(cljs.core.seq(specs__$1)){
var p = cljs.core.first(specs__$1);
var ret__$1 = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.conj.cljs$core$IFn$_invoke$arity$2(ret,p),cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(cljs.core.partial.cljs$core$IFn$_invoke$arity$2(cljs.core$macros.annotate_specs,annots),cljs.core.PersistentVector.EMPTY,cljs.core.group_by(cljs.core.first,cljs.core.take_while.cljs$core$IFn$_invoke$arity$2(cljs.core.seq_QMARK_,cljs.core.next(specs__$1)))));
var specs__$2 = cljs.core.drop_while.cljs$core$IFn$_invoke$arity$2(cljs.core.seq_QMARK_,cljs.core.next(specs__$1));
var G__7590 = ret__$1;
var G__7591 = specs__$2;
ret = G__7590;
specs__$1 = G__7591;
continue;
} else {
return ret;
}
break;
}
});

cljs.core$macros.dt__GT_et.cljs$lang$maxFixedArity = 4;

cljs.core$macros.collect_protocols = (function cljs$core$macros$collect_protocols(impls,env){
return cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentHashSet.EMPTY,cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__7592_SHARP_){
return new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(cljs.analyzer.resolve_var.cljs$core$IFn$_invoke$arity$2(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(env,new cljs.core.Keyword(null,"locals","locals",535295783)),p1__7592_SHARP_));
}),cljs.core.filter.cljs$core$IFn$_invoke$arity$2(cljs.core.symbol_QMARK_,impls)));
});
cljs.core$macros.build_positional_factory = (function cljs$core$macros$build_positional_factory(rsym,rname,fields){
var fn_name = cljs.core.with_meta(cljs.core.symbol.cljs$core$IFn$_invoke$arity$1([cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Symbol(null,"->","->",-2139605430,null)),cljs.core.str.cljs$core$IFn$_invoke$arity$1(rsym)].join('')),cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.meta(rsym),new cljs.core.Keyword(null,"factory","factory",63933746),new cljs.core.Keyword(null,"positional","positional",-203580463)));
var field_values = (cljs.core.truth_(new cljs.core.Keyword(null,"internal-ctor","internal-ctor",937392560).cljs$core$IFn$_invoke$arity$1(cljs.core.meta(rsym)))?cljs.core.conj.cljs$core$IFn$_invoke$arity$variadic(fields,null,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([null,null], 0)):fields);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","defn","cljs.core$macros/defn",-728332354,null)),(function (){var x__18265__auto__ = fn_name;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(fields))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"new","new",-444906321,null)),(function (){var x__18265__auto__ = rname;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([field_values], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.validate_fields = (function cljs$core$macros$validate_fields(case$,name,fields){
if(cljs.core.vector_QMARK_(fields)){
return null;
} else {
throw (new Error([cljs.core.str.cljs$core$IFn$_invoke$arity$1(case$)," ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(name),", no fields vector given."].join('')));
}
});
var ret__18490__auto___7604 = (function (){
/**
 * (deftype name [fields*]  options* specs*)
 * 
 *   Currently there are no options.
 * 
 *   Each spec consists of a protocol or interface name followed by zero
 *   or more method bodies:
 * 
 *   protocol-or-Object
 *   (methodName [args*] body)*
 * 
 *   The type will have the (by default, immutable) fields named by
 *   fields, which can have type hints. Protocols and methods
 *   are optional. The only methods that can be supplied are those
 *   declared in the protocols/interfaces.  Note that method bodies are
 *   not closures, the local environment includes only the named fields,
 *   and those fields can be accessed directly. Fields can be qualified
 *   with the metadata :mutable true at which point (set! afield aval) will be
 *   supported in method bodies. Note well that mutable fields are extremely
 *   difficult to use correctly, and are present only to facilitate the building
 *   of higherlevel constructs, such as ClojureScript's reference types, in
 *   ClojureScript itself. They are for experts only - if the semantics and
 *   implications of :mutable are not immediately apparent to you, you should not
 *   be using them.
 * 
 *   Method definitions take the form:
 * 
 *   (methodname [args*] body)
 * 
 *   The argument and return types can be hinted on the arg and
 *   methodname symbols. If not supplied, they will be inferred, so type
 *   hints should be reserved for disambiguation.
 * 
 *   Methods should be supplied for all methods of the desired
 *   protocol(s). You can also define overrides for methods of Object. Note that
 *   a parameter must be supplied to correspond to the target object
 *   ('this' in JavaScript parlance). Note also that recur calls to the method
 *   head should *not* pass the target object, it will be supplied
 *   automatically and can not be substituted.
 * 
 *   In the method bodies, the (unqualified) name can be used to name the
 *   class (for calls to new, instance? etc).
 * 
 *   One constructor will be defined, taking the designated fields.  Note
 *   that the field names __meta and __extmap are currently reserved and
 *   should not be used when defining your own types.
 * 
 *   Given (deftype TypeName ...), a factory function called ->TypeName
 *   will be defined, taking positional parameters for the fields
 */
cljs.core$macros.deftype = (function cljs$core$macros$deftype(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7605 = arguments.length;
var i__18451__auto___7606 = (0);
while(true){
if((i__18451__auto___7606 < len__18450__auto___7605)){
args__18453__auto__.push((arguments[i__18451__auto___7606]));

var G__7607 = (i__18451__auto___7606 + (1));
i__18451__auto___7606 = G__7607;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((4) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((4)),(0),null)):null);
return cljs.core$macros.deftype.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18454__auto__);
});

cljs.core$macros.deftype.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,t,fields,impls){
cljs.core$macros.validate_fields("deftype",t,fields);

var env = _AMPERSAND_env;
var r = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(cljs.analyzer.resolve_var.cljs$core$IFn$_invoke$arity$2(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(env,new cljs.core.Keyword(null,"locals","locals",535295783)),t));
var vec__7601 = cljs.core$macros.prepare_protocol_masks(env,impls);
var fpps = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7601,(0),null);
var pmasks = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7601,(1),null);
var protocols = cljs.core$macros.collect_protocols(impls,env);
var t__$1 = cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$6(t,cljs.core.assoc,new cljs.core.Keyword(null,"protocols","protocols",-5615896),protocols,new cljs.core.Keyword(null,"skip-protocol-flag","skip-protocol-flag",-1426798630),fpps);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"deftype*","deftype*",962659890,null)),(function (){var x__18265__auto__ = t__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = fields;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = pmasks;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = ((cljs.core.seq(impls))?cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","extend-type","cljs.core$macros/extend-type",1713295201,null)),(function (){var x__18265__auto__ = t__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core$macros.dt__GT_et.cljs$core$IFn$_invoke$arity$3(t__$1,impls,fields)], 0)))):null);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-getBasis",".-getBasis",-1306451468,null)),(function (){var x__18265__auto__ = t__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"quote","quote",1377916282,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(fields))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-cljs$lang$type",".-cljs$lang$type",-1029307724,null)),(function (){var x__18265__auto__ = t__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,true)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-cljs$lang$ctorStr",".-cljs$lang$ctorStr",-1820706991,null)),(function (){var x__18265__auto__ = t__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(r)].join('');
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-cljs$lang$ctorPrWriter",".-cljs$lang$ctorPrWriter",255834464,null)),(function (){var x__18265__auto__ = t__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7593__auto__","this__7593__auto__",1035047296,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"writer__7594__auto__","writer__7594__auto__",1897915687,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"opt__7595__auto__","opt__7595__auto__",455958756,null))], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-write","cljs.core$macros/-write",305468786,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"writer__7594__auto__","writer__7594__auto__",1897915687,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(r)].join('');
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core$macros.build_positional_factory(t__$1,r,fields);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = t__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.deftype.cljs$lang$maxFixedArity = (4);

cljs.core$macros.deftype.cljs$lang$applyTo = (function (seq7596){
var G__7597 = cljs.core.first(seq7596);
var seq7596__$1 = cljs.core.next(seq7596);
var G__7598 = cljs.core.first(seq7596__$1);
var seq7596__$2 = cljs.core.next(seq7596__$1);
var G__7599 = cljs.core.first(seq7596__$2);
var seq7596__$3 = cljs.core.next(seq7596__$2);
var G__7600 = cljs.core.first(seq7596__$3);
var seq7596__$4 = cljs.core.next(seq7596__$3);
return cljs.core$macros.deftype.cljs$core$IFn$_invoke$arity$variadic(G__7597,G__7598,G__7599,G__7600,seq7596__$4);
});

return null;
})()
;
cljs.core$macros.deftype.cljs$lang$macro = true;

/**
 * Do not use this directly - use defrecord
 */
cljs.core$macros.emit_defrecord = (function cljs$core$macros$emit_defrecord(env,tagname,rname,fields,impls){
var hinted_fields = fields;
var fields__$1 = cljs.core.vec(cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (hinted_fields){
return (function (p1__7608_SHARP_){
return cljs.core.with_meta(p1__7608_SHARP_,null);
});})(hinted_fields))
,fields));
var base_fields = fields__$1;
var pr_open = ["#",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.namespace(rname)),".",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(rname)),"{"].join('');
var fields__$2 = cljs.core.conj.cljs$core$IFn$_invoke$arity$variadic(fields__$1,new cljs.core.Symbol(null,"__meta","__meta",-946752628,null),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Symbol(null,"__extmap","__extmap",-1435580931,null),cljs.core.with_meta(new cljs.core.Symbol(null,"__hash","__hash",-1328796629,null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"mutable","mutable",875778266),true], null))], 0));
var gs = cljs.core.gensym.cljs$core$IFn$_invoke$arity$0();
var ksym = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("k");
var impls__$1 = cljs.core.concat.cljs$core$IFn$_invoke$arity$2(impls,new cljs.core.PersistentVector(null, 28, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"IRecord","IRecord",-903221169,null),new cljs.core.Symbol(null,"ICloneable","ICloneable",1882653160,null),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-clone","-clone",227130084,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7609__auto__","this__7609__auto__",511981196,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"new","new",-444906321,null)),(function (){var x__18265__auto__ = tagname;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([fields__$2], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),new cljs.core.Symbol(null,"IHash","IHash",-1495374645,null),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-hash","-hash",-630070274,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7610__auto__","this__7610__auto__",1487236499,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","caching-hash","cljs.core$macros/caching-hash",-1892393069,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7610__auto__","this__7610__auto__",1487236499,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"coll__7611__auto__","coll__7611__auto__",720206101,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","bit-xor","cljs.core$macros/bit-xor",1284619191,null)),(function (){var x__18265__auto__ = cljs.core.hash([cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.compiler.munge.cljs$core$IFn$_invoke$arity$1(rname))].join(''));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","hash-unordered-coll","cljs.core$macros/hash-unordered-coll",-716115986,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"coll__7611__auto__","coll__7611__auto__",720206101,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"__hash","__hash",-1328796629,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),new cljs.core.Symbol(null,"IEquiv","IEquiv",-1912850869,null),(function (){var this$ = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1(new cljs.core.Symbol(null,"this","this",1028897902,null));
var other = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1(new cljs.core.Symbol(null,"other","other",-1658642225,null));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-equiv","-equiv",320124272,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = this$;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = other;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","and","cljs.core$macros/and",48320334,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","some?","cljs.core$macros/some?",-253244523,null)),(function (){var x__18265__auto__ = other;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","identical?","cljs.core$macros/identical?",815580547,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-constructor",".-constructor",279801701,null)),(function (){var x__18265__auto__ = this$;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-constructor",".-constructor",279801701,null)),(function (){var x__18265__auto__ = other;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (this$,other,gs,ksym,hinted_fields,fields__$1,base_fields,pr_open,fields__$2){
return (function (field){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","=","cljs.core$macros/=",-2085967629,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"..","..",-300507420,null)),(function (){var x__18265__auto__ = this$;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core$macros.to_property(field);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"..","..",-300507420,null)),(function (){var x__18265__auto__ = other;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core$macros.to_property(field);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});})(this$,other,gs,ksym,hinted_fields,fields__$1,base_fields,pr_open,fields__$2))
,base_fields),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","=","cljs.core$macros/=",-2085967629,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-__extmap",".-__extmap",438409624,null)),(function (){var x__18265__auto__ = this$;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-__extmap",".-__extmap",438409624,null)),(function (){var x__18265__auto__ = other;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
})(),new cljs.core.Symbol(null,"IMeta","IMeta",1095313672,null),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-meta","-meta",494863156,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7612__auto__","this__7612__auto__",-1273315550,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"__meta","__meta",-946752628,null))], 0)))),new cljs.core.Symbol(null,"IWithMeta","IWithMeta",-509493158,null),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-with-meta","-with-meta",-1610713823,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7613__auto__","this__7613__auto__",926052788,null)),(function (){var x__18265__auto__ = gs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"new","new",-444906321,null)),(function (){var x__18265__auto__ = tagname;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.replace.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Symbol(null,"__meta","__meta",-946752628,null),gs], null),fields__$2)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),new cljs.core.Symbol(null,"ILookup","ILookup",784647298,null),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-lookup","-lookup",-1438393944,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7614__auto__","this__7614__auto__",1512889551,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"k__7615__auto__","k__7615__auto__",325436282,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-lookup","cljs.core$macros/-lookup",-2057987080,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7614__auto__","this__7614__auto__",1512889551,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"k__7615__auto__","k__7615__auto__",325436282,null)),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-lookup","-lookup",-1438393944,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7616__auto__","this__7616__auto__",-1592339580,null)),(function (){var x__18265__auto__ = ksym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"else__7617__auto__","else__7617__auto__",-571893474,null))], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","case","cljs.core$macros/case",-2131866965,null)),(function (){var x__18265__auto__ = ksym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic(((function (gs,ksym,hinted_fields,fields__$1,base_fields,pr_open,fields__$2){
return (function (f){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.keyword.cljs$core$IFn$_invoke$arity$1(f),f], null);
});})(gs,ksym,hinted_fields,fields__$1,base_fields,pr_open,fields__$2))
,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([base_fields], 0)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","get","cljs.core/get",-296075407,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"__extmap","__extmap",-1435580931,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = ksym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"else__7617__auto__","else__7617__auto__",-571893474,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),new cljs.core.Symbol(null,"ICounted","ICounted",-1705786327,null),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-count","-count",416049189,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7618__auto__","this__7618__auto__",1257841383,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","+","cljs.core$macros/+",-18260342,null)),(function (){var x__18265__auto__ = cljs.core.count(base_fields);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","count","cljs.core$macros/count",-1027674388,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"__extmap","__extmap",-1435580931,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),new cljs.core.Symbol(null,"ICollection","ICollection",-686709190,null),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-conj","-conj",1373798691,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7619__auto__","this__7619__auto__",1840623275,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"entry__7620__auto__","entry__7620__auto__",65555045,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","vector?","cljs.core$macros/vector?",-1623106701,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"entry__7620__auto__","entry__7620__auto__",65555045,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-assoc","cljs.core$macros/-assoc",-1071334638,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7619__auto__","this__7619__auto__",1840623275,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-nth","cljs.core$macros/-nth",357694713,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"entry__7620__auto__","entry__7620__auto__",65555045,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,(0))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-nth","cljs.core$macros/-nth",357694713,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"entry__7620__auto__","entry__7620__auto__",65555045,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,(1))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","reduce","cljs.core$macros/reduce",1976583152,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-conj","cljs.core$macros/-conj",1962460403,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7619__auto__","this__7619__auto__",1840623275,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"entry__7620__auto__","entry__7620__auto__",65555045,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),new cljs.core.Symbol(null,"IAssociative","IAssociative",-1306431882,null),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-assoc","-assoc",-416089758,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7621__auto__","this__7621__auto__",-286729888,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"k__7622__auto__","k__7622__auto__",1831471128,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = gs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","condp","cljs.core$macros/condp",431619047,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","keyword-identical?","cljs.core$macros/keyword-identical?",1391438930,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"k__7622__auto__","k__7622__auto__",1831471128,null)),cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic(((function (gs,ksym,hinted_fields,fields__$1,base_fields,pr_open,fields__$2){
return (function (fld){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.keyword.cljs$core$IFn$_invoke$arity$1(fld),cljs.core.list_STAR_.cljs$core$IFn$_invoke$arity$3(new cljs.core.Symbol(null,"new","new",-444906321,null),tagname,cljs.core.replace.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.createAsIfByAssoc([fld,gs,new cljs.core.Symbol(null,"__hash","__hash",-1328796629,null),null]),fields__$2))], null);
});})(gs,ksym,hinted_fields,fields__$1,base_fields,pr_open,fields__$2))
,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([base_fields], 0)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"new","new",-444906321,null)),(function (){var x__18265__auto__ = tagname;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.remove.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Symbol(null,"__hash","__hash",-1328796629,null),null,new cljs.core.Symbol(null,"__extmap","__extmap",-1435580931,null),null], null), null),fields__$2),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","assoc","cljs.core$macros/assoc",77460908,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"__extmap","__extmap",-1435580931,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"k__7622__auto__","k__7622__auto__",1831471128,null)),(function (){var x__18265__auto__ = gs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),new cljs.core.Symbol(null,"IMap","IMap",992876629,null),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-dissoc","-dissoc",1638079447,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7623__auto__","this__7623__auto__",510035590,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"k__7624__auto__","k__7624__auto__",1779564217,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","contains?","cljs.core$macros/contains?",-1324147536,null)),(function (){var x__18265__auto__ = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.hash_set,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.keyword,base_fields)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"k__7624__auto__","k__7624__auto__",1779564217,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","dissoc","cljs.core$macros/dissoc",-757944270,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-with-meta","cljs.core$macros/-with-meta",-489409807,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","into","cljs.core$macros/into",1173696890,null)),(function (){var x__18265__auto__ = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.array_map,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7623__auto__","this__7623__auto__",510035590,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"__meta","__meta",-946752628,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"k__7624__auto__","k__7624__auto__",1779564217,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"new","new",-444906321,null)),(function (){var x__18265__auto__ = tagname;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.remove.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Symbol(null,"__hash","__hash",-1328796629,null),null,new cljs.core.Symbol(null,"__extmap","__extmap",-1435580931,null),null], null), null),fields__$2),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","not-empty","cljs.core$macros/not-empty",1575535558,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","dissoc","cljs.core$macros/dissoc",-757944270,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"__extmap","__extmap",-1435580931,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"k__7624__auto__","k__7624__auto__",1779564217,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),new cljs.core.Symbol(null,"ISeqable","ISeqable",1349682102,null),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-seq","-seq",1019896831,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7626__auto__","this__7626__auto__",-926562124,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","seq","cljs.core$macros/seq",-1873195024,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","concat","cljs.core$macros/concat",-2028571073,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (gs,ksym,hinted_fields,fields__$1,base_fields,pr_open,fields__$2){
return (function (p1__7625_SHARP_){
return cljs.core._conj((function (){var x__18265__auto__ = cljs.core.keyword.cljs$core$IFn$_invoke$arity$1(p1__7625_SHARP_);
return cljs.core._conj((function (){var x__18265__auto____$1 = p1__7625_SHARP_;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),new cljs.core.Symbol("cljs.core$macros","vector","cljs.core$macros/vector",912237829,null));
});})(gs,ksym,hinted_fields,fields__$1,base_fields,pr_open,fields__$2))
,base_fields)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"__extmap","__extmap",-1435580931,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),new cljs.core.Symbol(null,"IIterable","IIterable",577191430,null),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-iterator","-iterator",310937281,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1((function (){var x__18265__auto__ = gs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"RecordIter.","RecordIter.",-265283060,null)),cljs.core._conj(cljs.core.List.EMPTY,(0)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = gs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.count(base_fields);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.keyword,base_fields)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"__extmap","__extmap",-1435580931,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-iterator","cljs.core$macros/-iterator",1908294769,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"__extmap","__extmap",-1435580931,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","nil-iter","cljs.core/nil-iter",-1712403690,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),new cljs.core.Symbol(null,"IPrintWithWriter","IPrintWithWriter",-1205316154,null),cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-pr-writer","-pr-writer",-445354136,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7628__auto__","this__7628__auto__",-471573849,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"writer__7629__auto__","writer__7629__auto__",1980076322,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"opts__7630__auto__","opts__7630__auto__",291568972,null))], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"pr-pair__7631__auto__","pr-pair__7631__auto__",-1359735569,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"keyval__7632__auto__","keyval__7632__auto__",983247841,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","pr-sequential-writer","cljs.core$macros/pr-sequential-writer",-1292021418,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"writer__7629__auto__","writer__7629__auto__",1980076322,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","pr-writer","cljs.core$macros/pr-writer",454326827,null)),cljs.core._conj(cljs.core.List.EMPTY,""),cljs.core._conj(cljs.core.List.EMPTY," "),cljs.core._conj(cljs.core.List.EMPTY,""),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"opts__7630__auto__","opts__7630__auto__",291568972,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"keyval__7632__auto__","keyval__7632__auto__",983247841,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","pr-sequential-writer","cljs.core$macros/pr-sequential-writer",-1292021418,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"writer__7629__auto__","writer__7629__auto__",1980076322,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"pr-pair__7631__auto__","pr-pair__7631__auto__",-1359735569,null)),(function (){var x__18265__auto__ = pr_open;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,", "),cljs.core._conj(cljs.core.List.EMPTY,"}"),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"opts__7630__auto__","opts__7630__auto__",291568972,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","concat","cljs.core$macros/concat",-2028571073,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (gs,ksym,hinted_fields,fields__$1,base_fields,pr_open,fields__$2){
return (function (p1__7627_SHARP_){
return cljs.core._conj((function (){var x__18265__auto__ = cljs.core.keyword.cljs$core$IFn$_invoke$arity$1(p1__7627_SHARP_);
return cljs.core._conj((function (){var x__18265__auto____$1 = p1__7627_SHARP_;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),new cljs.core.Symbol("cljs.core$macros","vector","cljs.core$macros/vector",912237829,null));
});})(gs,ksym,hinted_fields,fields__$1,base_fields,pr_open,fields__$2))
,base_fields)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"__extmap","__extmap",-1435580931,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))))], null));
var vec__7633 = cljs.core$macros.prepare_protocol_masks(env,impls__$1);
var fpps = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7633,(0),null);
var pmasks = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7633,(1),null);
var protocols = cljs.core$macros.collect_protocols(impls__$1,env);
var tagname__$1 = cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$6(tagname,cljs.core.assoc,new cljs.core.Keyword(null,"protocols","protocols",-5615896),protocols,new cljs.core.Keyword(null,"skip-protocol-flag","skip-protocol-flag",-1426798630),fpps);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"defrecord*","defrecord*",-1936366207,null)),(function (){var x__18265__auto__ = tagname__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = hinted_fields;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = pmasks;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","extend-type","cljs.core$macros/extend-type",1713295201,null)),(function (){var x__18265__auto__ = tagname__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core$macros.dt__GT_et.cljs$core$IFn$_invoke$arity$4(tagname__$1,impls__$1,fields__$2,true)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});
cljs.core$macros.build_map_factory = (function cljs$core$macros$build_map_factory(rsym,rname,fields){
var fn_name = cljs.core.with_meta(cljs.core.symbol.cljs$core$IFn$_invoke$arity$1([cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Symbol(null,"map->","map->",-999714600,null)),cljs.core.str.cljs$core$IFn$_invoke$arity$1(rsym)].join('')),cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(cljs.core.meta(rsym),new cljs.core.Keyword(null,"factory","factory",63933746),new cljs.core.Keyword(null,"map","map",1371690461)));
var ms = cljs.core.gensym.cljs$core$IFn$_invoke$arity$0();
var ks = cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.keyword,fields);
var getters = cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (fn_name,ms,ks){
return (function (k){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = k;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = ms;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});})(fn_name,ms,ks))
,ks);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","defn","cljs.core$macros/defn",-728332354,null)),(function (){var x__18265__auto__ = fn_name;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1((function (){var x__18265__auto__ = ms;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"new","new",-444906321,null)),(function (){var x__18265__auto__ = rname;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([getters,cljs.core._conj(cljs.core.List.EMPTY,null),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","not-empty","cljs.core$macros/not-empty",1575535558,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","dissoc","cljs.core$macros/dissoc",-757944270,null)),(function (){var x__18265__auto__ = ms;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([ks], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
var ret__18490__auto___7643 = (function (){
/**
 * (defrecord name [fields*]  options* specs*)
 * 
 *   Currently there are no options.
 * 
 *   Each spec consists of a protocol or interface name followed by zero
 *   or more method bodies:
 * 
 *   protocol-or-Object
 *   (methodName [args*] body)*
 * 
 *   The record will have the (immutable) fields named by
 *   fields, which can have type hints. Protocols and methods
 *   are optional. The only methods that can be supplied are those
 *   declared in the protocols.  Note that method bodies are
 *   not closures, the local environment includes only the named fields,
 *   and those fields can be accessed directly.
 * 
 *   Method definitions take the form:
 * 
 *   (methodname [args*] body)
 * 
 *   The argument and return types can be hinted on the arg and
 *   methodname symbols. If not supplied, they will be inferred, so type
 *   hints should be reserved for disambiguation.
 * 
 *   Methods should be supplied for all methods of the desired
 *   protocol(s). You can also define overrides for
 *   methods of Object. Note that a parameter must be supplied to
 *   correspond to the target object ('this' in JavaScript parlance). Note also
 *   that recur calls to the method head should *not* pass the target object, it
 *   will be supplied automatically and can not be substituted.
 * 
 *   In the method bodies, the (unqualified) name can be used to name the
 *   class (for calls to new, instance? etc).
 * 
 *   The type will have implementations of several ClojureScript
 *   protocol generated automatically: IMeta/IWithMeta (metadata support) and
 *   IMap, etc.
 * 
 *   In addition, defrecord will define type-and-value-based =,
 *   and will define ClojureScript IHash and IEquiv.
 * 
 *   Two constructors will be defined, one taking the designated fields
 *   followed by a metadata map (nil for none) and an extension field
 *   map (nil for none), and one taking only the fields (using nil for
 *   meta and extension fields). Note that the field names __meta
 *   and __extmap are currently reserved and should not be used when
 *   defining your own records.
 * 
 *   Given (defrecord TypeName ...), two factory functions will be
 *   defined: ->TypeName, taking positional parameters for the fields,
 *   and map->TypeName, taking a map of keywords to field values.
 */
cljs.core$macros.defrecord = (function cljs$core$macros$defrecord(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7644 = arguments.length;
var i__18451__auto___7645 = (0);
while(true){
if((i__18451__auto___7645 < len__18450__auto___7644)){
args__18453__auto__.push((arguments[i__18451__auto___7645]));

var G__7646 = (i__18451__auto___7645 + (1));
i__18451__auto___7645 = G__7646;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((4) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((4)),(0),null)):null);
return cljs.core$macros.defrecord.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18454__auto__);
});

cljs.core$macros.defrecord.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,rsym,fields,impls){
cljs.core$macros.validate_fields("defrecord",rsym,fields);

var rsym__$1 = cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(rsym,cljs.core.assoc,new cljs.core.Keyword(null,"internal-ctor","internal-ctor",937392560),true);
var r = cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(cljs.analyzer.resolve_var.cljs$core$IFn$_invoke$arity$2(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(_AMPERSAND_env,new cljs.core.Keyword(null,"locals","locals",535295783)),rsym__$1)),cljs.core.assoc,new cljs.core.Keyword(null,"internal-ctor","internal-ctor",937392560),true);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core$macros.emit_defrecord(_AMPERSAND_env,rsym__$1,r,fields,impls);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-getBasis",".-getBasis",-1306451468,null)),(function (){var x__18265__auto__ = r;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"quote","quote",1377916282,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(fields))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-cljs$lang$type",".-cljs$lang$type",-1029307724,null)),(function (){var x__18265__auto__ = r;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,true)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-cljs$lang$ctorPrSeq",".-cljs$lang$ctorPrSeq",41132414,null)),(function (){var x__18265__auto__ = r;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7636__auto__","this__7636__auto__",-1353174348,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","list","cljs.core/list",-1331406371,null)),(function (){var x__18265__auto__ = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(r)].join('');
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-cljs$lang$ctorPrWriter",".-cljs$lang$ctorPrWriter",255834464,null)),(function (){var x__18265__auto__ = r;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__7636__auto__","this__7636__auto__",-1353174348,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"writer__7637__auto__","writer__7637__auto__",-1895919430,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-write","cljs.core$macros/-write",305468786,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"writer__7637__auto__","writer__7637__auto__",-1895919430,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(r)].join('');
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core$macros.build_positional_factory(rsym__$1,r,fields);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core$macros.build_map_factory(rsym__$1,r,fields);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = r;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.defrecord.cljs$lang$maxFixedArity = (4);

cljs.core$macros.defrecord.cljs$lang$applyTo = (function (seq7638){
var G__7639 = cljs.core.first(seq7638);
var seq7638__$1 = cljs.core.next(seq7638);
var G__7640 = cljs.core.first(seq7638__$1);
var seq7638__$2 = cljs.core.next(seq7638__$1);
var G__7641 = cljs.core.first(seq7638__$2);
var seq7638__$3 = cljs.core.next(seq7638__$2);
var G__7642 = cljs.core.first(seq7638__$3);
var seq7638__$4 = cljs.core.next(seq7638__$3);
return cljs.core$macros.defrecord.cljs$core$IFn$_invoke$arity$variadic(G__7639,G__7640,G__7641,G__7642,seq7638__$4);
});

return null;
})()
;
cljs.core$macros.defrecord.cljs$lang$macro = true;

var ret__18490__auto___7674 = (function (){
/**
 * A protocol is a named set of named methods and their signatures:
 * 
 *   (defprotocol AProtocolName
 *  ;optional doc string
 *  "A doc string for AProtocol abstraction"
 * 
 *   ;method signatures
 *  (bar [this a b] "bar docs")
 *  (baz [this a] [this a b] [this a b c] "baz docs"))
 * 
 *   No implementations are provided. Docs can be specified for the
 *   protocol overall and for each method. The above yields a set of
 *   polymorphic functions and a protocol object. All are
 *   namespace-qualified by the ns enclosing the definition The resulting
 *   functions dispatch on the type of their first argument, which is
 *   required and corresponds to the implicit target object ('this' in
 *   JavaScript parlance). defprotocol is dynamic, has no special compile-time
 *   effect, and defines no new types.
 * 
 *   (defprotocol P
 *  (foo [this])
 *  (bar-me [this] [this y]))
 * 
 *   (deftype Foo [a b c]
 *  P
 *  (foo [this] a)
 *  (bar-me [this] b)
 *  (bar-me [this y] (+ c y)))
 * 
 *   (bar-me (Foo. 1 2 3) 42)
 *   => 45
 * 
 *   (foo
 *  (let [x 42]
 *    (reify P
 *      (foo [this] 17)
 *      (bar-me [this] x)
 *      (bar-me [this y] x))))
 *   => 17
 */
cljs.core$macros.defprotocol = (function cljs$core$macros$defprotocol(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7675 = arguments.length;
var i__18451__auto___7676 = (0);
while(true){
if((i__18451__auto___7676 < len__18450__auto___7675)){
args__18453__auto__.push((arguments[i__18451__auto___7676]));

var G__7677 = (i__18451__auto___7676 + (1));
i__18451__auto___7676 = G__7677;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.defprotocol.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.defprotocol.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,psym,doc_PLUS_methods){
var p = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(cljs.analyzer.resolve_var.cljs$core$IFn$_invoke$arity$2(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(_AMPERSAND_env,new cljs.core.Keyword(null,"locals","locals",535295783)),psym));
var vec__7653 = ((typeof cljs.core.first(doc_PLUS_methods) === 'string')?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.first(doc_PLUS_methods),cljs.core.next(doc_PLUS_methods)], null):new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [null,doc_PLUS_methods], null));
var doc = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7653,(0),null);
var methods$ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7653,(1),null);
var psym__$1 = cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$6(psym,cljs.core.assoc,new cljs.core.Keyword(null,"doc","doc",1913296891),doc,new cljs.core.Keyword(null,"protocol-symbol","protocol-symbol",1279552198),true);
var ns_name = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(_AMPERSAND_env));
var fqn = ((function (p,vec__7653,doc,methods$,psym__$1,ns_name){
return (function (n){
return cljs.core.symbol.cljs$core$IFn$_invoke$arity$1([cljs.core.str.cljs$core$IFn$_invoke$arity$1(ns_name),".",cljs.core.str.cljs$core$IFn$_invoke$arity$1(n)].join(''));
});})(p,vec__7653,doc,methods$,psym__$1,ns_name))
;
var prefix = cljs.core$macros.protocol_prefix(p);
var _ = (function (){var seq__7656 = cljs.core.seq(methods$);
var chunk__7657 = null;
var count__7658 = (0);
var i__7659 = (0);
while(true){
if((i__7659 < count__7658)){
var vec__7660 = chunk__7657.cljs$core$IIndexed$_nth$arity$2(null,i__7659);
var seq__7661 = cljs.core.seq(vec__7660);
var first__7662 = cljs.core.first(seq__7661);
var seq__7661__$1 = cljs.core.next(seq__7661);
var mname = first__7662;
var arities = seq__7661__$1;
if(cljs.core.truth_(cljs.core.some(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [(0),null], null), null),cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.count,cljs.core.filter.cljs$core$IFn$_invoke$arity$2(cljs.core.vector_QMARK_,arities))))){
throw (new Error(["Invalid protocol, ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(psym__$1)," defines method ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(mname)," with arity 0"].join('')));
} else {
}

var G__7678 = seq__7656;
var G__7679 = chunk__7657;
var G__7680 = count__7658;
var G__7681 = (i__7659 + (1));
seq__7656 = G__7678;
chunk__7657 = G__7679;
count__7658 = G__7680;
i__7659 = G__7681;
continue;
} else {
var temp__5459__auto__ = cljs.core.seq(seq__7656);
if(temp__5459__auto__){
var seq__7656__$1 = temp__5459__auto__;
if(cljs.core.chunked_seq_QMARK_(seq__7656__$1)){
var c__18254__auto__ = cljs.core.chunk_first(seq__7656__$1);
var G__7682 = cljs.core.chunk_rest(seq__7656__$1);
var G__7683 = c__18254__auto__;
var G__7684 = cljs.core.count(c__18254__auto__);
var G__7685 = (0);
seq__7656 = G__7682;
chunk__7657 = G__7683;
count__7658 = G__7684;
i__7659 = G__7685;
continue;
} else {
var vec__7663 = cljs.core.first(seq__7656__$1);
var seq__7664 = cljs.core.seq(vec__7663);
var first__7665 = cljs.core.first(seq__7664);
var seq__7664__$1 = cljs.core.next(seq__7664);
var mname = first__7665;
var arities = seq__7664__$1;
if(cljs.core.truth_(cljs.core.some(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [(0),null], null), null),cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.count,cljs.core.filter.cljs$core$IFn$_invoke$arity$2(cljs.core.vector_QMARK_,arities))))){
throw (new Error(["Invalid protocol, ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(psym__$1)," defines method ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(mname)," with arity 0"].join('')));
} else {
}

var G__7686 = cljs.core.next(seq__7656__$1);
var G__7687 = null;
var G__7688 = (0);
var G__7689 = (0);
seq__7656 = G__7686;
chunk__7657 = G__7687;
count__7658 = G__7688;
i__7659 = G__7689;
continue;
}
} else {
return null;
}
}
break;
}
})();
var expand_sig = ((function (p,vec__7653,doc,methods$,psym__$1,ns_name,fqn,prefix,_){
return (function (fname,slot,sig){
var sig__$1 = ((!(cljs.core.every_QMARK_(cljs.core.symbol_QMARK_,sig)))?cljs.core.mapv.cljs$core$IFn$_invoke$arity$2(((function (p,vec__7653,doc,methods$,psym__$1,ns_name,fqn,prefix,_){
return (function (arg){
if((arg instanceof cljs.core.Symbol)){
return arg;
} else {
if((cljs.core.map_QMARK_(arg)) && (!((new cljs.core.Keyword(null,"as","as",1148689641).cljs$core$IFn$_invoke$arity$1(arg) == null)))){
return new cljs.core.Keyword(null,"as","as",1148689641).cljs$core$IFn$_invoke$arity$1(arg);
} else {
return cljs.core.gensym.cljs$core$IFn$_invoke$arity$0();

}
}
});})(p,vec__7653,doc,methods$,psym__$1,ns_name,fqn,prefix,_))
,sig):sig);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = sig__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","and","cljs.core$macros/and",48320334,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","not","cljs.core$macros/not",325607907,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),(function (){var x__18265__auto__ = cljs.core.first(sig__$1);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","not","cljs.core$macros/not",325607907,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = cljs.core.first(sig__$1);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["-",cljs.core.str.cljs$core$IFn$_invoke$arity$1(slot)].join(''));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = cljs.core.first(sig__$1);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = slot;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),sig__$1], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7647__auto__","x__7647__auto__",1935979187,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),(function (){var x__18265__auto__ = cljs.core.first(sig__$1);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,null),(function (){var x__18265__auto__ = cljs.core.first(sig__$1);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"m__7648__auto__","m__7648__auto__",-1241701769,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unchecked-get","cljs.core$macros/unchecked-get",-1185166893,null)),(function (){var x__18265__auto__ = fqn(fname);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("goog","typeOf","goog/typeOf",539097255,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7647__auto__","x__7647__auto__",1935979187,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","if-not","cljs.core$macros/if-not",-1825285737,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"m__7648__auto__","m__7648__auto__",-1241701769,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"m__7648__auto__","m__7648__auto__",-1241701769,null)),sig__$1)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"m__7648__auto__","m__7648__auto__",-1241701769,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unchecked-get","cljs.core$macros/unchecked-get",-1185166893,null)),(function (){var x__18265__auto__ = fqn(fname);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,"_")], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","if-not","cljs.core$macros/if-not",-1825285737,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"m__7648__auto__","m__7648__auto__",-1241701769,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"m__7648__auto__","m__7648__auto__",-1241701769,null)),sig__$1)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"throw","throw",595905694,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","missing-protocol","cljs.core$macros/missing-protocol",722797257,null)),(function (){var x__18265__auto__ = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(psym__$1),".",cljs.core.str.cljs$core$IFn$_invoke$arity$1(fname)].join('');
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.first(sig__$1);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});})(p,vec__7653,doc,methods$,psym__$1,ns_name,fqn,prefix,_))
;
var psym__$2 = cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$5(psym__$1,cljs.core.update_in,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"jsdoc","jsdoc",1745183516)], null),cljs.core.conj,"@interface"),cljs.core.assoc_in,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"protocol-info","protocol-info",1471745843),new cljs.core.Keyword(null,"methods","methods",453930866)], null),cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (p,vec__7653,doc,methods$,psym__$1,ns_name,fqn,prefix,_,expand_sig){
return (function (p__7666){
var vec__7667 = p__7666;
var seq__7668 = cljs.core.seq(vec__7667);
var first__7669 = cljs.core.first(seq__7668);
var seq__7668__$1 = cljs.core.next(seq__7668);
var fname = first__7669;
var sigs = seq__7668__$1;
var doc__$1 = (function (){var doc__$1 = cljs.core.last(sigs);
if(typeof doc__$1 === 'string'){
return doc__$1;
} else {
return null;
}
})();
var sigs__$1 = cljs.core.take_while.cljs$core$IFn$_invoke$arity$2(cljs.core.vector_QMARK_,sigs);
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(fname,cljs.core.assoc,new cljs.core.Keyword(null,"doc","doc",1913296891),doc__$1),cljs.core.vec(sigs__$1)], null);
});})(p,vec__7653,doc,methods$,psym__$1,ns_name,fqn,prefix,_,expand_sig))
,methods$)));
var method = ((function (p,vec__7653,doc,methods$,psym__$1,ns_name,fqn,prefix,_,expand_sig,psym__$2){
return (function (p__7670){
var vec__7671 = p__7670;
var seq__7672 = cljs.core.seq(vec__7671);
var first__7673 = cljs.core.first(seq__7672);
var seq__7672__$1 = cljs.core.next(seq__7672);
var fname = first__7673;
var sigs = seq__7672__$1;
var doc__$1 = (function (){var doc__$1 = cljs.core.last(sigs);
if(typeof doc__$1 === 'string'){
return doc__$1;
} else {
return null;
}
})();
var sigs__$1 = cljs.core.take_while.cljs$core$IFn$_invoke$arity$2(cljs.core.vector_QMARK_,sigs);
var amp = (cljs.core.truth_(cljs.core.some(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Symbol(null,"&","&",-2144855648,null),null], null), null),cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.concat,sigs__$1)))?cljs.analyzer.warning(new cljs.core.Keyword(null,"protocol-with-variadic-method","protocol-with-variadic-method",-693368178),_AMPERSAND_env,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"protocol","protocol",652470118),psym__$2,new cljs.core.Keyword(null,"name","name",1843675177),fname], null)):null);
var slot = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1([cljs.core.str.cljs$core$IFn$_invoke$arity$1(prefix),cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name(fname))].join(''));
var fname__$1 = cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$6(fname,cljs.core.assoc,new cljs.core.Keyword(null,"protocol","protocol",652470118),p,new cljs.core.Keyword(null,"doc","doc",1913296891),doc__$1);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","defn","cljs.core$macros/defn",-728332354,null)),(function (){var x__18265__auto__ = fname__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (doc__$1,sigs__$1,amp,slot,fname__$1,vec__7671,seq__7672,first__7673,seq__7672__$1,fname,sigs,p,vec__7653,doc,methods$,psym__$1,ns_name,fqn,prefix,_,expand_sig,psym__$2){
return (function (sig){
return expand_sig(fname__$1,cljs.core.symbol.cljs$core$IFn$_invoke$arity$1([cljs.core.str.cljs$core$IFn$_invoke$arity$1(slot),"$arity$",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.count(sig))].join('')),sig);
});})(doc__$1,sigs__$1,amp,slot,fname__$1,vec__7671,seq__7672,first__7673,seq__7672__$1,fname,sigs,p,vec__7653,doc,methods$,psym__$1,ns_name,fqn,prefix,_,expand_sig,psym__$2))
,sigs__$1)], 0))));
});})(p,vec__7653,doc,methods$,psym__$1,ns_name,fqn,prefix,_,expand_sig,psym__$2))
;
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"*unchecked-if*","*unchecked-if*",1542408350,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,true)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"def","def",597100991,null)),(function (){var x__18265__auto__ = psym__$2;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"js*","js*",-1134233646,null)),cljs.core._conj(cljs.core.List.EMPTY,"function(){}"))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.map.cljs$core$IFn$_invoke$arity$2(method,methods$),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"*unchecked-if*","*unchecked-if*",1542408350,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,false)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.defprotocol.cljs$lang$maxFixedArity = (3);

cljs.core$macros.defprotocol.cljs$lang$applyTo = (function (seq7649){
var G__7650 = cljs.core.first(seq7649);
var seq7649__$1 = cljs.core.next(seq7649);
var G__7651 = cljs.core.first(seq7649__$1);
var seq7649__$2 = cljs.core.next(seq7649__$1);
var G__7652 = cljs.core.first(seq7649__$2);
var seq7649__$3 = cljs.core.next(seq7649__$2);
return cljs.core$macros.defprotocol.cljs$core$IFn$_invoke$arity$variadic(G__7650,G__7651,G__7652,seq7649__$3);
});

return null;
})()
;
cljs.core$macros.defprotocol.cljs$lang$macro = true;

var ret__18490__auto___7693 = /**
 * EXPERIMENTAL
 */
cljs.core$macros.implements_QMARK_ = (function cljs$core$macros$implements_QMARK_(_AMPERSAND_form,_AMPERSAND_env,psym,x){
var p = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(cljs.analyzer.resolve_var.cljs$core$IFn$_invoke$arity$2(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(_AMPERSAND_env,new cljs.core.Keyword(null,"locals","locals",535295783)),psym));
var prefix = cljs.core$macros.protocol_prefix(p);
var xsym = cljs.core$macros.bool_expr(cljs.core.gensym.cljs$core$IFn$_invoke$arity$0());
var vec__7690 = (cljs.core$macros.fast_path_protocols.cljs$core$IFn$_invoke$arity$1 ? cljs.core$macros.fast_path_protocols.cljs$core$IFn$_invoke$arity$1(p) : cljs.core$macros.fast_path_protocols.call(null,p));
var part = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7690,(0),null);
var bit = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7690,(1),null);
var msym = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["-cljs$lang$protocol_mask$partition",cljs.core.str.cljs$core$IFn$_invoke$arity$1(part),"$"].join(''));
if(!((x instanceof cljs.core.Symbol))){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = xsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = xsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","or","cljs.core$macros/or",1346243648,null)),(function (){var x__18265__auto__ = (cljs.core.truth_(bit)?cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unsafe-bit-and","cljs.core$macros/unsafe-bit-and",1803731600,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = xsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = msym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = bit;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))):false);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","identical?","cljs.core$macros/identical?",815580547,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","PROTOCOL_SENTINEL","cljs.core/PROTOCOL_SENTINEL",210209696,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = xsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["-",cljs.core.str.cljs$core$IFn$_invoke$arity$1(prefix)].join(''));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,true),cljs.core._conj(cljs.core.List.EMPTY,false)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,false)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","if-not","cljs.core$macros/if-not",-1825285737,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","or","cljs.core$macros/or",1346243648,null)),(function (){var x__18265__auto__ = (cljs.core.truth_(bit)?cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unsafe-bit-and","cljs.core$macros/unsafe-bit-and",1803731600,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = msym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = bit;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))):false);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","identical?","cljs.core$macros/identical?",815580547,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","PROTOCOL_SENTINEL","cljs.core/PROTOCOL_SENTINEL",210209696,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["-",cljs.core.str.cljs$core$IFn$_invoke$arity$1(prefix)].join(''));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,true),cljs.core._conj(cljs.core.List.EMPTY,false)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,false)], 0))));
}
});
cljs.core$macros.implements_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7697 = /**
 * Returns true if x satisfies the protocol
 */
cljs.core$macros.satisfies_QMARK_ = (function cljs$core$macros$satisfies_QMARK_(_AMPERSAND_form,_AMPERSAND_env,psym,x){
var p = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(cljs.analyzer.resolve_var.cljs$core$IFn$_invoke$arity$2(cljs.core.dissoc.cljs$core$IFn$_invoke$arity$2(_AMPERSAND_env,new cljs.core.Keyword(null,"locals","locals",535295783)),psym));
var prefix = cljs.core$macros.protocol_prefix(p);
var xsym = cljs.core$macros.bool_expr(cljs.core.gensym.cljs$core$IFn$_invoke$arity$0());
var vec__7694 = (cljs.core$macros.fast_path_protocols.cljs$core$IFn$_invoke$arity$1 ? cljs.core$macros.fast_path_protocols.cljs$core$IFn$_invoke$arity$1(p) : cljs.core$macros.fast_path_protocols.call(null,p));
var part = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7694,(0),null);
var bit = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7694,(1),null);
var msym = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["-cljs$lang$protocol_mask$partition",cljs.core.str.cljs$core$IFn$_invoke$arity$1(part),"$"].join(''));
if(!((x instanceof cljs.core.Symbol))){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = xsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","if-not","cljs.core$macros/if-not",-1825285737,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),(function (){var x__18265__auto__ = xsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","or","cljs.core$macros/or",1346243648,null)),(function (){var x__18265__auto__ = (cljs.core.truth_(bit)?cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unsafe-bit-and","cljs.core$macros/unsafe-bit-and",1803731600,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = xsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = msym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = bit;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))):false);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","identical?","cljs.core$macros/identical?",815580547,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","PROTOCOL_SENTINEL","cljs.core/PROTOCOL_SENTINEL",210209696,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = xsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["-",cljs.core.str.cljs$core$IFn$_invoke$arity$1(prefix)].join(''));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,true),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","coercive-not","cljs.core$macros/coercive-not",115999987,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = xsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = msym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","native-satisfies?","cljs.core/native-satisfies?",1482305036,null)),(function (){var x__18265__auto__ = psym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = xsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,false)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","native-satisfies?","cljs.core/native-satisfies?",1482305036,null)),(function (){var x__18265__auto__ = psym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = xsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","if-not","cljs.core$macros/if-not",-1825285737,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","or","cljs.core$macros/or",1346243648,null)),(function (){var x__18265__auto__ = (cljs.core.truth_(bit)?cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unsafe-bit-and","cljs.core$macros/unsafe-bit-and",1803731600,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = msym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = bit;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))):false);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","identical?","cljs.core$macros/identical?",815580547,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","PROTOCOL_SENTINEL","cljs.core/PROTOCOL_SENTINEL",210209696,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["-",cljs.core.str.cljs$core$IFn$_invoke$arity$1(prefix)].join(''));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,true),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","coercive-not","cljs.core$macros/coercive-not",115999987,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = msym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","native-satisfies?","cljs.core/native-satisfies?",1482305036,null)),(function (){var x__18265__auto__ = psym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,false)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","native-satisfies?","cljs.core/native-satisfies?",1482305036,null)),(function (){var x__18265__auto__ = psym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
}
});
cljs.core$macros.satisfies_QMARK_.cljs$lang$macro = true;

var ret__18490__auto___7701 = (function (){
/**
 * Takes a body of expressions that returns an ISeq or nil, and yields
 *   a ISeqable object that will invoke the body only the first time seq
 *   is called, and will cache the result and return it on all subsequent
 *   seq calls.
 */
cljs.core$macros.lazy_seq = (function cljs$core$macros$lazy_seq(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7702 = arguments.length;
var i__18451__auto___7703 = (0);
while(true){
if((i__18451__auto___7703 < len__18450__auto___7702)){
args__18453__auto__.push((arguments[i__18451__auto___7703]));

var G__7704 = (i__18451__auto___7703 + (1));
i__18451__auto___7703 = G__7704;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.lazy_seq.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.lazy_seq.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,body){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"new","new",-444906321,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","LazySeq","cljs.core/LazySeq",1986389673,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,null),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
});

cljs.core$macros.lazy_seq.cljs$lang$maxFixedArity = (2);

cljs.core$macros.lazy_seq.cljs$lang$applyTo = (function (seq7698){
var G__7699 = cljs.core.first(seq7698);
var seq7698__$1 = cljs.core.next(seq7698);
var G__7700 = cljs.core.first(seq7698__$1);
var seq7698__$2 = cljs.core.next(seq7698__$1);
return cljs.core$macros.lazy_seq.cljs$core$IFn$_invoke$arity$variadic(G__7699,G__7700,seq7698__$2);
});

return null;
})()
;
cljs.core$macros.lazy_seq.cljs$lang$macro = true;

var ret__18490__auto___7708 = (function (){
/**
 * Takes a body of expressions and yields a Delay object that will
 *   invoke the body only the first time it is forced (with force or deref/@), and
 *   will cache the result and return it on all subsequent force
 *   calls.
 */
cljs.core$macros.delay = (function cljs$core$macros$delay(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7709 = arguments.length;
var i__18451__auto___7710 = (0);
while(true){
if((i__18451__auto___7710 < len__18450__auto___7709)){
args__18453__auto__.push((arguments[i__18451__auto___7710]));

var G__7711 = (i__18451__auto___7710 + (1));
i__18451__auto___7710 = G__7711;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.delay.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.delay.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,body){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"new","new",-444906321,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","Delay","cljs.core/Delay",-21574999,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
});

cljs.core$macros.delay.cljs$lang$maxFixedArity = (2);

cljs.core$macros.delay.cljs$lang$applyTo = (function (seq7705){
var G__7706 = cljs.core.first(seq7705);
var seq7705__$1 = cljs.core.next(seq7705);
var G__7707 = cljs.core.first(seq7705__$1);
var seq7705__$2 = cljs.core.next(seq7705__$1);
return cljs.core$macros.delay.cljs$core$IFn$_invoke$arity$variadic(G__7706,G__7707,seq7705__$2);
});

return null;
})()
;
cljs.core$macros.delay.cljs$lang$macro = true;

var ret__18490__auto___7720 = (function (){
/**
 * binding => var-symbol temp-value-expr
 * 
 *   Temporarily redefines vars while executing the body.  The
 *   temp-value-exprs will be evaluated and each resulting value will
 *   replace in parallel the root value of its var.  After the body is
 *   executed, the root values of all the vars will be set back to their
 *   old values. Useful for mocking out functions during testing.
 */
cljs.core$macros.with_redefs = (function cljs$core$macros$with_redefs(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7721 = arguments.length;
var i__18451__auto___7722 = (0);
while(true){
if((i__18451__auto___7722 < len__18450__auto___7721)){
args__18453__auto__.push((arguments[i__18451__auto___7722]));

var G__7723 = (i__18451__auto___7722 + (1));
i__18451__auto___7722 = G__7723;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.with_redefs.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.with_redefs.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,body){
var names = cljs.core.take_nth.cljs$core$IFn$_invoke$arity$2((2),bindings);
var vals = cljs.core.take_nth.cljs$core$IFn$_invoke$arity$2((2),cljs.core.drop.cljs$core$IFn$_invoke$arity$2((1),bindings));
var tempnames = cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.comp.cljs$core$IFn$_invoke$arity$2(cljs.core.gensym,cljs.core.name),names);
var binds = cljs.core.map.cljs$core$IFn$_invoke$arity$3(cljs.core.vector,names,vals);
var resets = cljs.core.reverse(cljs.core.map.cljs$core$IFn$_invoke$arity$3(cljs.core.vector,names,tempnames));
var bind_value = ((function (names,vals,tempnames,binds,resets){
return (function (p__7716){
var vec__7717 = p__7716;
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7717,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7717,(1),null);
return cljs.core._conj((function (){var x__18265__auto__ = k;
return cljs.core._conj((function (){var x__18265__auto____$1 = v;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),new cljs.core.Symbol(null,"set!","set!",250714521,null));
});})(names,vals,tempnames,binds,resets))
;
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core.interleave.cljs$core$IFn$_invoke$arity$2(tempnames,names)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.map.cljs$core$IFn$_invoke$arity$2(bind_value,binds),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"try","try",-1273693247,null)),body,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"finally","finally",-1065347064,null)),cljs.core.map.cljs$core$IFn$_invoke$arity$2(bind_value,resets))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.with_redefs.cljs$lang$maxFixedArity = (3);

cljs.core$macros.with_redefs.cljs$lang$applyTo = (function (seq7712){
var G__7713 = cljs.core.first(seq7712);
var seq7712__$1 = cljs.core.next(seq7712);
var G__7714 = cljs.core.first(seq7712__$1);
var seq7712__$2 = cljs.core.next(seq7712__$1);
var G__7715 = cljs.core.first(seq7712__$2);
var seq7712__$3 = cljs.core.next(seq7712__$2);
return cljs.core$macros.with_redefs.cljs$core$IFn$_invoke$arity$variadic(G__7713,G__7714,G__7715,seq7712__$3);
});

return null;
})()
;
cljs.core$macros.with_redefs.cljs$lang$macro = true;

var ret__18490__auto___7728 = (function (){
/**
 * binding => var-symbol init-expr
 * 
 *   Creates new bindings for the (already-existing) vars, with the
 *   supplied initial values, executes the exprs in an implicit do, then
 *   re-establishes the bindings that existed before.  The new bindings
 *   are made in parallel (unlike let); all init-exprs are evaluated
 *   before the vars are bound to their new values.
 */
cljs.core$macros.binding = (function cljs$core$macros$binding(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7729 = arguments.length;
var i__18451__auto___7730 = (0);
while(true){
if((i__18451__auto___7730 < len__18450__auto___7729)){
args__18453__auto__.push((arguments[i__18451__auto___7730]));

var G__7731 = (i__18451__auto___7730 + (1));
i__18451__auto___7730 = G__7731;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.binding.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.binding.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,body){
var names = cljs.core.take_nth.cljs$core$IFn$_invoke$arity$2((2),bindings);
cljs.analyzer.confirm_bindings(_AMPERSAND_env,names);

return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","with-redefs","cljs.core$macros/with-redefs",1489217801,null)),(function (){var x__18265__auto__ = bindings;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
});

cljs.core$macros.binding.cljs$lang$maxFixedArity = (3);

cljs.core$macros.binding.cljs$lang$applyTo = (function (seq7724){
var G__7725 = cljs.core.first(seq7724);
var seq7724__$1 = cljs.core.next(seq7724);
var G__7726 = cljs.core.first(seq7724__$1);
var seq7724__$2 = cljs.core.next(seq7724__$1);
var G__7727 = cljs.core.first(seq7724__$2);
var seq7724__$3 = cljs.core.next(seq7724__$2);
return cljs.core$macros.binding.cljs$core$IFn$_invoke$arity$variadic(G__7725,G__7726,G__7727,seq7724__$3);
});

return null;
})()
;
cljs.core$macros.binding.cljs$lang$macro = true;

var ret__18490__auto___7744 = (function (){
/**
 * Takes a binary predicate, an expression, and a set of clauses.
 *   Each clause can take the form of either:
 * 
 *   test-expr result-expr
 * 
 *   test-expr :>> result-fn
 * 
 *   Note :>> is an ordinary keyword.
 * 
 *   For each clause, (pred test-expr expr) is evaluated. If it returns
 *   logical true, the clause is a match. If a binary clause matches, the
 *   result-expr is returned, if a ternary clause matches, its result-fn,
 *   which must be a unary function, is called with the result of the
 *   predicate as its argument, the result of that call being the return
 *   value of condp. A single default expression can follow the clauses,
 *   and its value will be returned if no clause matches. If no default
 *   expression is provided and no clause matches, an
 *   IllegalArgumentException is thrown.
 */
cljs.core$macros.condp = (function cljs$core$macros$condp(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7745 = arguments.length;
var i__18451__auto___7746 = (0);
while(true){
if((i__18451__auto___7746 < len__18450__auto___7745)){
args__18453__auto__.push((arguments[i__18451__auto___7746]));

var G__7747 = (i__18451__auto___7746 + (1));
i__18451__auto___7746 = G__7747;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((4) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((4)),(0),null)):null);
return cljs.core$macros.condp.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18454__auto__);
});

cljs.core$macros.condp.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,pred,expr,clauses){
var gpred = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("pred__");
var gexpr = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("expr__");
var emit = ((function (gpred,gexpr){
return (function cljs$core$macros$emit(pred__$1,expr__$1,args){
var vec__7738 = cljs.core.split_at(((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,">>",">>",-277509267),cljs.core.second(args)))?(3):(2)),args);
var vec__7741 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7738,(0),null);
var a = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7741,(0),null);
var b = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7741,(1),null);
var c = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7741,(2),null);
var clause = vec__7741;
var more = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7738,(1),null);
var n = cljs.core.count(clause);
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((0),n)){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"throw","throw",595905694,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"java.lang.Error.","java.lang.Error.",108378905,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","str","cljs.core/str",-1971828991,null)),cljs.core._conj(cljs.core.List.EMPTY,"No matching clause: "),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = expr__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((1),n)){
return a;
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((2),n)){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = pred__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = a;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = expr__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = b;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs$core$macros$emit(pred__$1,expr__$1,more);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","if-let","cljs.core$macros/if-let",1291543946,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"p__7732__auto__","p__7732__auto__",-1653771963,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = pred__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = a;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = expr__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = c;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"p__7732__auto__","p__7732__auto__",-1653771963,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs$core$macros$emit(pred__$1,expr__$1,more);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));

}
}
}
});})(gpred,gexpr))
;
var gres = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("res__");
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = gpred;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = pred;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = gexpr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = emit(gpred,gexpr,clauses);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.condp.cljs$lang$maxFixedArity = (4);

cljs.core$macros.condp.cljs$lang$applyTo = (function (seq7733){
var G__7734 = cljs.core.first(seq7733);
var seq7733__$1 = cljs.core.next(seq7733);
var G__7735 = cljs.core.first(seq7733__$1);
var seq7733__$2 = cljs.core.next(seq7733__$1);
var G__7736 = cljs.core.first(seq7733__$2);
var seq7733__$3 = cljs.core.next(seq7733__$2);
var G__7737 = cljs.core.first(seq7733__$3);
var seq7733__$4 = cljs.core.next(seq7733__$3);
return cljs.core$macros.condp.cljs$core$IFn$_invoke$arity$variadic(G__7734,G__7735,G__7736,G__7737,seq7733__$4);
});

return null;
})()
;
cljs.core$macros.condp.cljs$lang$macro = true;

cljs.core$macros.assoc_test = (function cljs$core$macros$assoc_test(m,test,expr,env){
if(cljs.core.contains_QMARK_(m,test)){
throw (new Error(["Duplicate case test constant '",cljs.core.str.cljs$core$IFn$_invoke$arity$1(test),"'",cljs.core.str.cljs$core$IFn$_invoke$arity$1((cljs.core.truth_(new cljs.core.Keyword(null,"line","line",212345235).cljs$core$IFn$_invoke$arity$1(env))?[" on line ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"line","line",212345235).cljs$core$IFn$_invoke$arity$1(env))," ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.analyzer._STAR_cljs_file_STAR_)].join(''):null))].join('')));
} else {
return cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(m,test,expr);
}
});
cljs.core$macros.const_QMARK_ = (function cljs$core$macros$const_QMARK_(env,x){
var m = (function (){var and__17759__auto__ = cljs.core.list_QMARK_(x);
if(and__17759__auto__){
return cljs.analyzer.resolve_var.cljs$core$IFn$_invoke$arity$2(env,cljs.core.last(x));
} else {
return and__17759__auto__;
}
})();
if(cljs.core.truth_(m)){
return cljs.core.get.cljs$core$IFn$_invoke$arity$2(m,new cljs.core.Keyword(null,"const","const",1709929842));
} else {
return null;
}
});
var ret__18490__auto___7768 = (function (){
/**
 * Takes an expression, and a set of clauses.
 * 
 *   Each clause can take the form of either:
 * 
 *   test-constant result-expr
 * 
 *   (test-constant1 ... test-constantN)  result-expr
 * 
 *   The test-constants are not evaluated. They must be compile-time
 *   literals, and need not be quoted.  If the expression is equal to a
 *   test-constant, the corresponding result-expr is returned. A single
 *   default expression can follow the clauses, and its value will be
 *   returned if no clause matches. If no default expression is provided
 *   and no clause matches, an Error is thrown.
 * 
 *   Unlike cond and condp, case does a constant-time dispatch, the
 *   clauses are not considered sequentially.  All manner of constant
 *   expressions are acceptable in case, including numbers, strings,
 *   symbols, keywords, and (ClojureScript) composites thereof. Note that since
 *   lists are used to group multiple constants that map to the same
 *   expression, a vector can be used to match a list if needed. The
 *   test-constants need not be all of the same type.
 */
cljs.core$macros.case$ = (function cljs$core$macros$case(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7769 = arguments.length;
var i__18451__auto___7770 = (0);
while(true){
if((i__18451__auto___7770 < len__18450__auto___7769)){
args__18453__auto__.push((arguments[i__18451__auto___7770]));

var G__7771 = (i__18451__auto___7770 + (1));
i__18451__auto___7770 = G__7771;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.case$.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.case$.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,e,clauses){
var esym = cljs.core.gensym.cljs$core$IFn$_invoke$arity$0();
var default$ = ((cljs.core.odd_QMARK_(cljs.core.count(clauses)))?cljs.core.last(clauses):cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"throw","throw",595905694,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"java.lang.Error.","java.lang.Error.",108378905,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","str","cljs.core/str",-1971828991,null)),cljs.core._conj(cljs.core.List.EMPTY,"No matching clause: "),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = esym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
var env = _AMPERSAND_env;
var pairs = cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(((function (esym,default$,env){
return (function (m,p__7756){
var vec__7757 = p__7756;
var test = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7757,(0),null);
var expr = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7757,(1),null);
if(cljs.core.seq_QMARK_(test)){
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3(((function (vec__7757,test,expr,esym,default$,env){
return (function (m__$1,test__$1){
var test__$2 = (((test__$1 instanceof cljs.core.Symbol))?cljs.core._conj((function (){var x__18265__auto__ = test__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),new cljs.core.Symbol(null,"quote","quote",1377916282,null)):test__$1);
return cljs.core$macros.assoc_test(m__$1,test__$2,expr,env);
});})(vec__7757,test,expr,esym,default$,env))
,m,test);
} else {
if((test instanceof cljs.core.Symbol)){
return cljs.core$macros.assoc_test(m,cljs.core._conj((function (){var x__18265__auto__ = test;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),new cljs.core.Symbol(null,"quote","quote",1377916282,null)),expr,env);
} else {
return cljs.core$macros.assoc_test(m,test,expr,env);

}
}
});})(esym,default$,env))
,cljs.core.PersistentArrayMap.EMPTY,cljs.core.partition.cljs$core$IFn$_invoke$arity$2((2),clauses));
var tests = cljs.core.keys(pairs);
if(cljs.core.every_QMARK_(cljs.core.some_fn.cljs$core$IFn$_invoke$arity$variadic(cljs.core.number_QMARK_,cljs.core.string_QMARK_,cljs.core.fnil.cljs$core$IFn$_invoke$arity$2(cljs.core.char_QMARK_,new cljs.core.Keyword(null,"nonchar","nonchar",-421759703)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([((function (esym,default$,env,pairs,tests){
return (function (p1__7748_SHARP_){
return cljs.core$macros.const_QMARK_(env,p1__7748_SHARP_);
});})(esym,default$,env,pairs,tests))
], 0)),tests)){
var no_default = ((cljs.core.odd_QMARK_(cljs.core.count(clauses)))?cljs.core.butlast(clauses):clauses);
var tests__$1 = cljs.core.mapv.cljs$core$IFn$_invoke$arity$2(((function (no_default,esym,default$,env,pairs,tests){
return (function (p1__7749_SHARP_){
if(cljs.core.seq_QMARK_(p1__7749_SHARP_)){
return cljs.core.vec(p1__7749_SHARP_);
} else {
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__7749_SHARP_], null);
}
});})(no_default,esym,default$,env,pairs,tests))
,cljs.core.take_nth.cljs$core$IFn$_invoke$arity$2((2),no_default));
var thens = cljs.core.vec(cljs.core.take_nth.cljs$core$IFn$_invoke$arity$2((2),cljs.core.drop.cljs$core$IFn$_invoke$arity$2((1),no_default)));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = esym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = e;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"case*","case*",-1938255072,null)),(function (){var x__18265__auto__ = esym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = tests__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = thens;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = default$;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
if(cljs.core.every_QMARK_(cljs.core.keyword_QMARK_,tests)){
var no_default = ((cljs.core.odd_QMARK_(cljs.core.count(clauses)))?cljs.core.butlast(clauses):clauses);
var kw_str = ((function (no_default,esym,default$,env,pairs,tests){
return (function (p1__7750_SHARP_){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(p1__7750_SHARP_)].join('').substring((1));
});})(no_default,esym,default$,env,pairs,tests))
;
var tests__$1 = cljs.core.mapv.cljs$core$IFn$_invoke$arity$2(((function (no_default,kw_str,esym,default$,env,pairs,tests){
return (function (p1__7751_SHARP_){
if(cljs.core.seq_QMARK_(p1__7751_SHARP_)){
return cljs.core.mapv.cljs$core$IFn$_invoke$arity$2(kw_str,p1__7751_SHARP_);
} else {
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [kw_str(p1__7751_SHARP_)], null);
}
});})(no_default,kw_str,esym,default$,env,pairs,tests))
,cljs.core.take_nth.cljs$core$IFn$_invoke$arity$2((2),no_default));
var thens = cljs.core.vec(cljs.core.take_nth.cljs$core$IFn$_invoke$arity$2((2),cljs.core.drop.cljs$core$IFn$_invoke$arity$2((1),no_default)));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = esym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = e;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = esym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","keyword?","cljs.core$macros/keyword?",1362730141,null)),(function (){var x__18265__auto__ = esym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-fqn",".-fqn",1246113027,null)),(function (){var x__18265__auto__ = esym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"case*","case*",-1938255072,null)),(function (){var x__18265__auto__ = esym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = tests__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = thens;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = default$;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = esym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = e;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","cond","cljs.core$macros/cond",1626318471,null)),cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic(((function (esym,default$,env,pairs,tests){
return (function (p__7764){
var vec__7765 = p__7764;
var m = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7765,(0),null);
var c = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7765,(1),null);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","=","cljs.core/=",-1891498332,null)),(function (){var x__18265__auto__ = m;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = esym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = c;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});})(esym,default$,env,pairs,tests))
,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([pairs], 0)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"else","else",-1508377146)),(function (){var x__18265__auto__ = default$;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));

}
}
});

cljs.core$macros.case$.cljs$lang$maxFixedArity = (3);

cljs.core$macros.case$.cljs$lang$applyTo = (function (seq7752){
var G__7753 = cljs.core.first(seq7752);
var seq7752__$1 = cljs.core.next(seq7752);
var G__7754 = cljs.core.first(seq7752__$1);
var seq7752__$2 = cljs.core.next(seq7752__$1);
var G__7755 = cljs.core.first(seq7752__$2);
var seq7752__$3 = cljs.core.next(seq7752__$2);
return cljs.core$macros.case$.cljs$core$IFn$_invoke$arity$variadic(G__7753,G__7754,G__7755,seq7752__$3);
});

return null;
})()
;
cljs.core$macros.case$.cljs$lang$macro = true;

var ret__18490__auto___7772 = cljs.core$macros.when_assert = (function cljs$core$macros$when_assert(_AMPERSAND_form,_AMPERSAND_env,x){
if(cljs.core._STAR_assert_STAR_){
return x;
} else {
return null;
}
});
cljs.core$macros.when_assert.cljs$lang$macro = true;

var ret__18490__auto___7775 = (function (){
/**
 * Evaluates expr and throws an exception if it does not evaluate to
 *   logical true.
 */
cljs.core$macros.assert = (function cljs$core$macros$assert(var_args){
var G__7774 = arguments.length;
switch (G__7774) {
case 3:
return cljs.core$macros.assert.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros.assert.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1((arguments.length - (2)))].join('')));

}
});

cljs.core$macros.assert.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,x){
if(cljs.core._STAR_assert_STAR_){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when-not","cljs.core$macros/when-not",-764302244,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"throw","throw",595905694,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"java.lang.Error.","java.lang.Error.",108378905,null)),(function (){var x__18265__auto__ = ["Assert failed: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([x], 0)))].join('');
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
return null;
}
});

cljs.core$macros.assert.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,x,message){
if(cljs.core._STAR_assert_STAR_){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when-not","cljs.core$macros/when-not",-764302244,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"throw","throw",595905694,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"java.lang.Error.","java.lang.Error.",108378905,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","str","cljs.core/str",-1971828991,null)),cljs.core._conj(cljs.core.List.EMPTY,"Assert failed: "),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = message;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,"\n"),(function (){var x__18265__auto__ = cljs.core.pr_str.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([x], 0));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
return null;
}
});

cljs.core$macros.assert.cljs$lang$maxFixedArity = 4;

return null;
})()
;
cljs.core$macros.assert.cljs$lang$macro = true;

var ret__18490__auto___7813 = /**
 * List comprehension. Takes a vector of one or more
 * binding-form/collection-expr pairs, each followed by zero or more
 * modifiers, and yields a lazy sequence of evaluations of expr.
 * Collections are iterated in a nested fashion, rightmost fastest,
 * and nested coll-exprs can refer to bindings created in prior
 * binding-forms.  Supported modifiers are: :let [binding-form expr ...],
 * :while test, :when test.
 * 
 *   (take 100 (for [x (range 100000000) y (range 1000000) :while (< y x)]  [x y]))
 */
cljs.core$macros.for$ = (function cljs$core$macros$for(_AMPERSAND_form,_AMPERSAND_env,seq_exprs,body_expr){
if(cljs.core.vector_QMARK_(seq_exprs)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("for requires a vector for its binding",cljs.core.PersistentArrayMap.EMPTY);
}

if(cljs.core.even_QMARK_(cljs.core.count(seq_exprs))){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("for requires an even number of forms in binding vector",cljs.core.PersistentArrayMap.EMPTY);
}


var to_groups = (function (seq_exprs__$1){
return cljs.core.reduce.cljs$core$IFn$_invoke$arity$3((function (groups,p__7782){
var vec__7783 = p__7782;
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7783,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7783,(1),null);
if((k instanceof cljs.core.Keyword)){
return cljs.core.conj.cljs$core$IFn$_invoke$arity$2(cljs.core.pop(groups),cljs.core.conj.cljs$core$IFn$_invoke$arity$2(cljs.core.peek(groups),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [k,v], null)));
} else {
return cljs.core.conj.cljs$core$IFn$_invoke$arity$2(groups,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [k,v], null));
}
}),cljs.core.PersistentVector.EMPTY,cljs.core.partition.cljs$core$IFn$_invoke$arity$2((2),seq_exprs__$1));
});
var err = ((function (to_groups){
return (function() { 
var G__7814__delegate = function (msg){
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2(cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.str,msg),cljs.core.PersistentArrayMap.EMPTY);
};
var G__7814 = function (var_args){
var msg = null;
if (arguments.length > 0) {
var G__7815__i = 0, G__7815__a = new Array(arguments.length -  0);
while (G__7815__i < G__7815__a.length) {G__7815__a[G__7815__i] = arguments[G__7815__i + 0]; ++G__7815__i;}
  msg = new cljs.core.IndexedSeq(G__7815__a,0,null);
} 
return G__7814__delegate.call(this,msg);};
G__7814.cljs$lang$maxFixedArity = 0;
G__7814.cljs$lang$applyTo = (function (arglist__7816){
var msg = cljs.core.seq(arglist__7816);
return G__7814__delegate(msg);
});
G__7814.cljs$core$IFn$_invoke$arity$variadic = G__7814__delegate;
return G__7814;
})()
;})(to_groups))
;
var emit_bind = ((function (to_groups,err){
return (function cljs$core$macros$for_$_emit_bind(p__7786){
var vec__7787 = p__7786;
var seq__7788 = cljs.core.seq(vec__7787);
var first__7789 = cljs.core.first(seq__7788);
var seq__7788__$1 = cljs.core.next(seq__7788);
var vec__7790 = first__7789;
var seq__7791 = cljs.core.seq(vec__7790);
var first__7792 = cljs.core.first(seq__7791);
var seq__7791__$1 = cljs.core.next(seq__7791);
var bind = first__7792;
var first__7792__$1 = cljs.core.first(seq__7791__$1);
var seq__7791__$2 = cljs.core.next(seq__7791__$1);
var expr = first__7792__$1;
var mod_pairs = seq__7791__$2;
var vec__7793 = seq__7788__$1;
var vec__7796 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7793,(0),null);
var _ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7796,(0),null);
var next_expr = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7796,(1),null);
var next_groups = vec__7793;
var giter = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("iter__");
var gxs = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("s__");
var do_mod = ((function (giter,gxs,vec__7787,seq__7788,first__7789,seq__7788__$1,vec__7790,seq__7791,first__7792,seq__7791__$1,bind,first__7792__$1,seq__7791__$2,expr,mod_pairs,vec__7793,vec__7796,_,next_expr,next_groups,to_groups,err){
return (function cljs$core$macros$for_$_emit_bind_$_do_mod(p__7799){
var vec__7800 = p__7799;
var seq__7801 = cljs.core.seq(vec__7800);
var first__7802 = cljs.core.first(seq__7801);
var seq__7801__$1 = cljs.core.next(seq__7801);
var vec__7803 = first__7802;
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7803,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7803,(1),null);
var pair = vec__7803;
var etc = seq__7801__$1;
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,new cljs.core.Keyword(null,"let","let",-1282412701))){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = v;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs$core$macros$for_$_emit_bind_$_do_mod(etc);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,new cljs.core.Keyword(null,"while","while",963117786))){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when","cljs.core$macros/when",328457725,null)),(function (){var x__18265__auto__ = v;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs$core$macros$for_$_emit_bind_$_do_mod(etc);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,new cljs.core.Keyword(null,"when","when",-576417306))){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = v;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs$core$macros$for_$_emit_bind_$_do_mod(etc);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"recur","recur",1202958259,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","rest","cljs.core$macros/rest",-218381740,null)),(function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
if((k instanceof cljs.core.Keyword)){
return err.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["Invalid 'for' keyword ",k], 0));
} else {
if(next_groups){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"iterys__7777__auto__","iterys__7777__auto__",1761720152,null)),(function (){var x__18265__auto__ = cljs$core$macros$for_$_emit_bind(next_groups);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"fs__7778__auto__","fs__7778__auto__",-2053518560,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","seq","cljs.core$macros/seq",-1873195024,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"iterys__7777__auto__","iterys__7777__auto__",1761720152,null)),(function (){var x__18265__auto__ = next_expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"fs__7778__auto__","fs__7778__auto__",-2053518560,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","concat","cljs.core$macros/concat",-2028571073,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"fs__7778__auto__","fs__7778__auto__",-2053518560,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = giter;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","rest","cljs.core$macros/rest",-218381740,null)),(function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"recur","recur",1202958259,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","rest","cljs.core$macros/rest",-218381740,null)),(function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","cons","cljs.core$macros/cons",238615766,null)),(function (){var x__18265__auto__ = body_expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = giter;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","rest","cljs.core$macros/rest",-218381740,null)),(function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));

}
}
}
}
}
});})(giter,gxs,vec__7787,seq__7788,first__7789,seq__7788__$1,vec__7790,seq__7791,first__7792,seq__7791__$1,bind,first__7792__$1,seq__7791__$2,expr,mod_pairs,vec__7793,vec__7796,_,next_expr,next_groups,to_groups,err))
;
if(next_groups){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = giter;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1((function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","lazy-seq","cljs.core$macros/lazy-seq",806482650,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","loop","cljs.core$macros/loop",1731108390,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when-first","cljs.core$macros/when-first",-840670160,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = bind;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = do_mod(mod_pairs);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
var gi = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("i__");
var gb = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("b__");
var do_cmod = ((function (gi,gb,giter,gxs,do_mod,vec__7787,seq__7788,first__7789,seq__7788__$1,vec__7790,seq__7791,first__7792,seq__7791__$1,bind,first__7792__$1,seq__7791__$2,expr,mod_pairs,vec__7793,vec__7796,_,next_expr,next_groups,to_groups,err){
return (function cljs$core$macros$for_$_emit_bind_$_do_cmod(p__7806){
var vec__7807 = p__7806;
var seq__7808 = cljs.core.seq(vec__7807);
var first__7809 = cljs.core.first(seq__7808);
var seq__7808__$1 = cljs.core.next(seq__7808);
var vec__7810 = first__7809;
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7810,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7810,(1),null);
var pair = vec__7810;
var etc = seq__7808__$1;
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,new cljs.core.Keyword(null,"let","let",-1282412701))){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = v;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs$core$macros$for_$_emit_bind_$_do_cmod(etc);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,new cljs.core.Keyword(null,"while","while",963117786))){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when","cljs.core$macros/when",328457725,null)),(function (){var x__18265__auto__ = v;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs$core$macros$for_$_emit_bind_$_do_cmod(etc);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,new cljs.core.Keyword(null,"when","when",-576417306))){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = v;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs$core$macros$for_$_emit_bind_$_do_cmod(etc);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"recur","recur",1202958259,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unchecked-inc","cljs.core$macros/unchecked-inc",-1615365330,null)),(function (){var x__18265__auto__ = gi;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
if((k instanceof cljs.core.Keyword)){
return err.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["Invalid 'for' keyword ",k], 0));
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","chunk-append","cljs.core$macros/chunk-append",-997100519,null)),(function (){var x__18265__auto__ = gb;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = body_expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"recur","recur",1202958259,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unchecked-inc","cljs.core$macros/unchecked-inc",-1615365330,null)),(function (){var x__18265__auto__ = gi;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));

}
}
}
}
});})(gi,gb,giter,gxs,do_mod,vec__7787,seq__7788,first__7789,seq__7788__$1,vec__7790,seq__7791,first__7792,seq__7791__$1,bind,first__7792__$1,seq__7791__$2,expr,mod_pairs,vec__7793,vec__7796,_,next_expr,next_groups,to_groups,err))
;
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = giter;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1((function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","lazy-seq","cljs.core$macros/lazy-seq",806482650,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","loop","cljs.core$macros/loop",1731108390,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when-let","cljs.core$macros/when-let",-2004472648,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","seq","cljs.core$macros/seq",-1873195024,null)),(function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","chunked-seq?","cljs.core$macros/chunked-seq?",-647408632,null)),(function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"c__7779__auto__","c__7779__auto__",1658248340,null)),(function (){var x__18265__auto__ = cljs.core.with_meta(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","chunk-first","cljs.core$macros/chunk-first",-1224455408,null)),(function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))),cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.array_map,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"file","file",-1269645878)),cljs.core._conj(cljs.core.List.EMPTY,"cljs/core$macros.cljc"),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"line","line",212345235)),cljs.core._conj(cljs.core.List.EMPTY,2383),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"column","column",2078222095)),cljs.core._conj(cljs.core.List.EMPTY,52),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"end-line","end-line",1837326455)),cljs.core._conj(cljs.core.List.EMPTY,2383),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"end-column","end-column",1425389514)),cljs.core._conj(cljs.core.List.EMPTY,82),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"tag","tag",-1290361223)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","not-native","cljs.core$macros/not-native",-1929204382,null))], 0))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"size__7780__auto__","size__7780__auto__",1891617502,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","count","cljs.core$macros/count",-1027674388,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"c__7779__auto__","c__7779__auto__",1658248340,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = gb;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","chunk-buffer","cljs.core$macros/chunk-buffer",208623439,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"size__7780__auto__","size__7780__auto__",1891617502,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","coercive-boolean","cljs.core$macros/coercive-boolean",-450758280,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","loop","cljs.core$macros/loop",1731108390,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = gi;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,(0))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","<","cljs.core$macros/<",371512596,null)),(function (){var x__18265__auto__ = gi;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"size__7780__auto__","size__7780__auto__",1891617502,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = bind;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-nth","cljs.core$macros/-nth",357694713,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"c__7779__auto__","c__7779__auto__",1658248340,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = gi;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = do_cmod(mod_pairs);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,true)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","chunk-cons","cljs.core$macros/chunk-cons",79793773,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","chunk","cljs.core$macros/chunk",133340627,null)),(function (){var x__18265__auto__ = gb;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = giter;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","chunk-rest","cljs.core$macros/chunk-rest",-54580004,null)),(function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","chunk-cons","cljs.core$macros/chunk-cons",79793773,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","chunk","cljs.core$macros/chunk",133340627,null)),(function (){var x__18265__auto__ = gb;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = bind;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","first","cljs.core$macros/first",-999127023,null)),(function (){var x__18265__auto__ = gxs;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = do_mod(mod_pairs);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
}
});})(to_groups,err))
;
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"iter__7781__auto__","iter__7781__auto__",1773503943,null)),(function (){var x__18265__auto__ = emit_bind(to_groups(seq_exprs));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"iter__7781__auto__","iter__7781__auto__",1773503943,null)),(function (){var x__18265__auto__ = cljs.core.second(seq_exprs);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.for$.cljs$lang$macro = true;

var ret__18490__auto___7822 = (function (){
/**
 * Repeatedly executes body (presumably for side-effects) with
 *   bindings and filtering as provided by "for".  Does not retain
 *   the head of the sequence. Returns nil.
 */
cljs.core$macros.doseq = (function cljs$core$macros$doseq(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7823 = arguments.length;
var i__18451__auto___7824 = (0);
while(true){
if((i__18451__auto___7824 < len__18450__auto___7823)){
args__18453__auto__.push((arguments[i__18451__auto___7824]));

var G__7825 = (i__18451__auto___7824 + (1));
i__18451__auto___7824 = G__7825;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.doseq.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.doseq.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,seq_exprs,body){
if(cljs.core.vector_QMARK_(seq_exprs)){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("doseq requires a vector for its binding",cljs.core.PersistentArrayMap.EMPTY);
}

if(cljs.core.even_QMARK_(cljs.core.count(seq_exprs))){
} else {
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2("doseq requires an even number of forms in binding vector",cljs.core.PersistentArrayMap.EMPTY);
}


var err = (function() { 
var G__7826__delegate = function (msg){
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2(cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.str,msg),cljs.core.PersistentArrayMap.EMPTY);
};
var G__7826 = function (var_args){
var msg = null;
if (arguments.length > 0) {
var G__7827__i = 0, G__7827__a = new Array(arguments.length -  0);
while (G__7827__i < G__7827__a.length) {G__7827__a[G__7827__i] = arguments[G__7827__i + 0]; ++G__7827__i;}
  msg = new cljs.core.IndexedSeq(G__7827__a,0,null);
} 
return G__7826__delegate.call(this,msg);};
G__7826.cljs$lang$maxFixedArity = 0;
G__7826.cljs$lang$applyTo = (function (arglist__7828){
var msg = cljs.core.seq(arglist__7828);
return G__7826__delegate(msg);
});
G__7826.cljs$core$IFn$_invoke$arity$variadic = G__7826__delegate;
return G__7826;
})()
;
var step = ((function (err){
return (function cljs$core$macros$step(recform,exprs){
if(cljs.core.not(exprs)){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [true,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),body)))], null);
} else {
var k = cljs.core.first(exprs);
var v = cljs.core.second(exprs);
var seqsym = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("seq__");
var recform__$1 = (((k instanceof cljs.core.Keyword))?recform:cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"recur","recur",1202958259,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","next","cljs.core$macros/next",-1478604758,null)),(function (){var x__18265__auto__ = seqsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,null),cljs.core._conj(cljs.core.List.EMPTY,(0)),cljs.core._conj(cljs.core.List.EMPTY,(0))], 0)))));
var steppair = cljs$core$macros$step(recform__$1,cljs.core.nnext(exprs));
var needrec = (steppair.cljs$core$IFn$_invoke$arity$1 ? steppair.cljs$core$IFn$_invoke$arity$1((0)) : steppair.call(null,(0)));
var subform = (steppair.cljs$core$IFn$_invoke$arity$1 ? steppair.cljs$core$IFn$_invoke$arity$1((1)) : steppair.call(null,(1)));
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,new cljs.core.Keyword(null,"let","let",-1282412701))){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [needrec,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = v;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = subform;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))))], null);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,new cljs.core.Keyword(null,"while","while",963117786))){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [false,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when","cljs.core$macros/when",328457725,null)),(function (){var x__18265__auto__ = v;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = subform;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(cljs.core.truth_(needrec)?new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [recform__$1], null):null)], 0))))], null);
} else {
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(k,new cljs.core.Keyword(null,"when","when",-576417306))){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [false,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = v;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),(function (){var x__18265__auto__ = subform;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(cljs.core.truth_(needrec)?new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [recform__$1], null):null)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = recform__$1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))))], null);
} else {
if((k instanceof cljs.core.Keyword)){
return err.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2(["Invalid 'doseq' keyword",k], 0));
} else {
var chunksym = cljs.core.with_meta(cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("chunk__"),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol(null,"not-native","not-native",-236392494,null)], null));
var countsym = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("count__");
var isym = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("i__");
var recform_chunk = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"recur","recur",1202958259,null)),(function (){var x__18265__auto__ = seqsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = chunksym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = countsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unchecked-inc","cljs.core$macros/unchecked-inc",-1615365330,null)),(function (){var x__18265__auto__ = isym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
var steppair_chunk = cljs$core$macros$step(recform_chunk,cljs.core.nnext(exprs));
var subform_chunk = (steppair_chunk.cljs$core$IFn$_invoke$arity$1 ? steppair_chunk.cljs$core$IFn$_invoke$arity$1((1)) : steppair_chunk.call(null,(1)));
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [true,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","loop","cljs.core$macros/loop",1731108390,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = seqsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","seq","cljs.core$macros/seq",-1873195024,null)),(function (){var x__18265__auto__ = v;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = chunksym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null),(function (){var x__18265__auto__ = countsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,(0)),(function (){var x__18265__auto__ = isym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,(0))], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","coercive-boolean","cljs.core$macros/coercive-boolean",-450758280,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","<","cljs.core$macros/<",371512596,null)),(function (){var x__18265__auto__ = isym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = countsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = k;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-nth","cljs.core$macros/-nth",357694713,null)),(function (){var x__18265__auto__ = chunksym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = isym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = subform_chunk;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(cljs.core.truth_(needrec)?new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [recform_chunk], null):null)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when-let","cljs.core$macros/when-let",-2004472648,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = seqsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","seq","cljs.core$macros/seq",-1873195024,null)),(function (){var x__18265__auto__ = seqsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","chunked-seq?","cljs.core$macros/chunked-seq?",-647408632,null)),(function (){var x__18265__auto__ = seqsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"c__7817__auto__","c__7817__auto__",-557940817,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","chunk-first","cljs.core$macros/chunk-first",-1224455408,null)),(function (){var x__18265__auto__ = seqsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"recur","recur",1202958259,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","chunk-rest","cljs.core$macros/chunk-rest",-54580004,null)),(function (){var x__18265__auto__ = seqsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"c__7817__auto__","c__7817__auto__",-557940817,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","count","cljs.core$macros/count",-1027674388,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"c__7817__auto__","c__7817__auto__",-557940817,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,(0))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = k;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","first","cljs.core$macros/first",-999127023,null)),(function (){var x__18265__auto__ = seqsym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = subform;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(cljs.core.truth_(needrec)?new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [recform__$1], null):null)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))))], null);

}
}
}
}
}
});})(err))
;
return cljs.core.nth.cljs$core$IFn$_invoke$arity$2(step(null,cljs.core.seq(seq_exprs)),(1));
});

cljs.core$macros.doseq.cljs$lang$maxFixedArity = (3);

cljs.core$macros.doseq.cljs$lang$applyTo = (function (seq7818){
var G__7819 = cljs.core.first(seq7818);
var seq7818__$1 = cljs.core.next(seq7818);
var G__7820 = cljs.core.first(seq7818__$1);
var seq7818__$2 = cljs.core.next(seq7818__$1);
var G__7821 = cljs.core.first(seq7818__$2);
var seq7818__$3 = cljs.core.next(seq7818__$2);
return cljs.core$macros.doseq.cljs$core$IFn$_invoke$arity$variadic(G__7819,G__7820,G__7821,seq7818__$3);
});

return null;
})()
;
cljs.core$macros.doseq.cljs$lang$macro = true;

var ret__18490__auto___7832 = (function (){
cljs.core$macros.array = (function cljs$core$macros$array(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7833 = arguments.length;
var i__18451__auto___7834 = (0);
while(true){
if((i__18451__auto___7834 < len__18450__auto___7833)){
args__18453__auto__.push((arguments[i__18451__auto___7834]));

var G__7835 = (i__18451__auto___7834 + (1));
i__18451__auto___7834 = G__7835;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.array.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.array.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,rest){
var xs_str = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.str,cljs.core.interpose.cljs$core$IFn$_invoke$arity$2(",",cljs.core.take.cljs$core$IFn$_invoke$arity$2(cljs.core.count(rest),cljs.core.repeat.cljs$core$IFn$_invoke$arity$1("~{}"))));
return cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(cljs.core.list_STAR_.cljs$core$IFn$_invoke$arity$3(new cljs.core.Symbol(null,"js*","js*",-1134233646,null),["[",cljs.core.str.cljs$core$IFn$_invoke$arity$1(xs_str),"]"].join(''),rest),cljs.core.assoc,new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol(null,"array","array",-440182315,null));
});

cljs.core$macros.array.cljs$lang$maxFixedArity = (2);

cljs.core$macros.array.cljs$lang$applyTo = (function (seq7829){
var G__7830 = cljs.core.first(seq7829);
var seq7829__$1 = cljs.core.next(seq7829);
var G__7831 = cljs.core.first(seq7829__$1);
var seq7829__$2 = cljs.core.next(seq7829__$1);
return cljs.core$macros.array.cljs$core$IFn$_invoke$arity$variadic(G__7830,G__7831,seq7829__$2);
});

return null;
})()
;
cljs.core$macros.array.cljs$lang$macro = true;

var ret__18490__auto___7846 = (function (){
cljs.core$macros.make_array = (function cljs$core$macros$make_array(var_args){
var G__7845 = arguments.length;
switch (G__7845) {
case 3:
return cljs.core$macros.make_array.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core$macros.make_array.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7848 = arguments.length;
var i__18451__auto___7849 = (0);
while(true){
if((i__18451__auto___7849 < len__18450__auto___7848)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7849]));

var G__7850 = (i__18451__auto___7849 + (1));
i__18451__auto___7849 = G__7850;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((4)),(0),null));
return cljs.core$macros.make_array.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18470__auto__);

}
});

cljs.core$macros.make_array.cljs$core$IFn$_invoke$arity$3 = (function (_AMPERSAND_form,_AMPERSAND_env,size){
return cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(((typeof size === 'number')?cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","array","cljs.core$macros/array",49650437,null)),cljs.core.take.cljs$core$IFn$_invoke$arity$2(size,cljs.core.repeat.cljs$core$IFn$_invoke$arity$1(null))))):cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"Array.","Array.",1235619490,null)),(function (){var x__18265__auto__ = size;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())))),cljs.core.assoc,new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol(null,"array","array",-440182315,null));
});

cljs.core$macros.make_array.cljs$core$IFn$_invoke$arity$4 = (function (_AMPERSAND_form,_AMPERSAND_env,type,size){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","make-array","cljs.core/make-array",-1802166799,null)),(function (){var x__18265__auto__ = size;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});

cljs.core$macros.make_array.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,type,size,more_sizes){
return cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"dims__7836__auto__","dims__7836__auto__",1519318452,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","list","cljs.core$macros/list",-1408486806,null)),more_sizes)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"dimarray__7837__auto__","dimarray__7837__auto__",-1093559740,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","make-array","cljs.core/make-array",-1802166799,null)),(function (){var x__18265__auto__ = size;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","dotimes","cljs.core$macros/dotimes",-1407597661,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"i__7838__auto__","i__7838__auto__",-1238071031,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","alength","cljs.core$macros/alength",-683052937,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"dimarray__7837__auto__","dimarray__7837__auto__",-1093559740,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","aset","cljs.core$macros/aset",-693176374,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"dimarray__7837__auto__","dimarray__7837__auto__",-1093559740,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"i__7838__auto__","i__7838__auto__",-1238071031,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","apply","cljs.core$macros/apply",1565934668,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","make-array","cljs.core/make-array",-1802166799,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,null),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"dims__7836__auto__","dims__7836__auto__",1519318452,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"dimarray__7837__auto__","dimarray__7837__auto__",-1093559740,null))], 0)))),cljs.core.assoc,new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol(null,"array","array",-440182315,null));
});

cljs.core$macros.make_array.cljs$lang$applyTo = (function (seq7840){
var G__7841 = cljs.core.first(seq7840);
var seq7840__$1 = cljs.core.next(seq7840);
var G__7842 = cljs.core.first(seq7840__$1);
var seq7840__$2 = cljs.core.next(seq7840__$1);
var G__7843 = cljs.core.first(seq7840__$2);
var seq7840__$3 = cljs.core.next(seq7840__$2);
var G__7844 = cljs.core.first(seq7840__$3);
var seq7840__$4 = cljs.core.next(seq7840__$3);
return cljs.core$macros.make_array.cljs$core$IFn$_invoke$arity$variadic(G__7841,G__7842,G__7843,G__7844,seq7840__$4);
});

cljs.core$macros.make_array.cljs$lang$maxFixedArity = (4);

return null;
})()
;
cljs.core$macros.make_array.cljs$lang$macro = true;

var ret__18490__auto___7859 = (function (){
cljs.core$macros.list = (function cljs$core$macros$list(var_args){
var G__7857 = arguments.length;
switch (G__7857) {
case 2:
return cljs.core$macros.list.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7861 = arguments.length;
var i__18451__auto___7862 = (0);
while(true){
if((i__18451__auto___7862 < len__18450__auto___7861)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7862]));

var G__7863 = (i__18451__auto___7862 + (1));
i__18451__auto___7862 = G__7863;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((3)),(0),null));
return cljs.core$macros.list.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18470__auto__);

}
});

cljs.core$macros.list.cljs$core$IFn$_invoke$arity$2 = (function (_AMPERSAND_form,_AMPERSAND_env){
return cljs.core.list(new cljs.core.Symbol(null,".-EMPTY",".-EMPTY",-471586691,null),new cljs.core.Symbol("cljs.core","List","cljs.core/List",1708954352,null));
});

cljs.core$macros.list.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,xs){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"constant","constant",-379609303),new cljs.core.Keyword(null,"op","op",-1882987955).cljs$core$IFn$_invoke$arity$1((function (){var _STAR_cljs_warnings_STAR_7858 = cljs.analyzer._STAR_cljs_warnings_STAR_;
cljs.analyzer._STAR_cljs_warnings_STAR_ = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"invoke-ctor","invoke-ctor",-756052704),new cljs.core.Keyword(null,"extending-base-js-type","extending-base-js-type",432787264),new cljs.core.Keyword(null,"unsupported-preprocess-value","unsupported-preprocess-value",1635147105),new cljs.core.Keyword(null,"redef","redef",1032704258),new cljs.core.Keyword(null,"js-shadowed-by-local","js-shadowed-by-local",198048291),new cljs.core.Keyword(null,"unsupported-js-module-type","unsupported-js-module-type",1806206180),new cljs.core.Keyword(null,"invalid-protocol-symbol","invalid-protocol-symbol",86246948),new cljs.core.Keyword(null,"protocol-duped-method","protocol-duped-method",15128166),new cljs.core.Keyword(null,"munged-namespace","munged-namespace",-101986199),new cljs.core.Keyword(null,"single-segment-namespace","single-segment-namespace",-491924469),new cljs.core.Keyword(null,"fn-arity","fn-arity",-403576501),new cljs.core.Keyword(null,"infer-warning","infer-warning",-1600671410),new cljs.core.Keyword(null,"variadic-max-arity","variadic-max-arity",-14288402),new cljs.core.Keyword(null,"protocol-with-variadic-method","protocol-with-variadic-method",-693368178),new cljs.core.Keyword(null,"fn-deprecated","fn-deprecated",626877647),new cljs.core.Keyword(null,"undeclared-var","undeclared-var",-1624364944),new cljs.core.Keyword(null,"preamble-missing","preamble-missing",220974801),new cljs.core.Keyword(null,"undeclared-protocol-symbol","undeclared-protocol-symbol",462882867),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"invalid-array-access","invalid-array-access",-1747132236),new cljs.core.Keyword(null,"unprovided","unprovided",-652330764),new cljs.core.Keyword(null,"ns-var-clash","ns-var-clash",-280494668),new cljs.core.Keyword(null,"undeclared-ns","undeclared-ns",-1589012812),new cljs.core.Keyword(null,"undeclared-ns-form","undeclared-ns-form",-248024137),new cljs.core.Keyword(null,"invalid-arithmetic","invalid-arithmetic",1917075065),new cljs.core.Keyword(null,"protocol-deprecated","protocol-deprecated",103233497),new cljs.core.Keyword(null,"fn-var","fn-var",1086204730),new cljs.core.Keyword(null,"protocol-impl-recur-with-target","protocol-impl-recur-with-target",-1648321574),new cljs.core.Keyword(null,"overload-arity","overload-arity",823206044),new cljs.core.Keyword(null,"protocol-multiple-impls","protocol-multiple-impls",794179260),new cljs.core.Keyword(null,"redef-in-file","redef-in-file",-476530788),new cljs.core.Keyword(null,"protocol-invalid-method","protocol-invalid-method",522647516),new cljs.core.Keyword(null,"extend-type-invalid-method-shape","extend-type-invalid-method-shape",1424103549),new cljs.core.Keyword(null,"multiple-variadic-overloads","multiple-variadic-overloads",1110059837),new cljs.core.Keyword(null,"protocol-impl-with-variadic-method","protocol-impl-with-variadic-method",-319321217)],[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false]);

try{return cljs.analyzer.analyze.cljs$core$IFn$_invoke$arity$2(_AMPERSAND_env,x);
}finally {cljs.analyzer._STAR_cljs_warnings_STAR_ = _STAR_cljs_warnings_STAR_7858;
}})()))){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-conj","cljs.core$macros/-conj",1962460403,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","list","cljs.core$macros/list",-1408486806,null)),xs)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7851__auto__","x__7851__auto__",1399893171,null)),(function (){var x__18265__auto__ = x;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-conj","cljs.core$macros/-conj",1962460403,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","list","cljs.core$macros/list",-1408486806,null)),xs)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__7851__auto__","x__7851__auto__",1399893171,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
}
});

cljs.core$macros.list.cljs$lang$applyTo = (function (seq7853){
var G__7854 = cljs.core.first(seq7853);
var seq7853__$1 = cljs.core.next(seq7853);
var G__7855 = cljs.core.first(seq7853__$1);
var seq7853__$2 = cljs.core.next(seq7853__$1);
var G__7856 = cljs.core.first(seq7853__$2);
var seq7853__$3 = cljs.core.next(seq7853__$2);
return cljs.core$macros.list.cljs$core$IFn$_invoke$arity$variadic(G__7854,G__7855,G__7856,seq7853__$3);
});

cljs.core$macros.list.cljs$lang$maxFixedArity = (3);

return null;
})()
;
cljs.core$macros.list.cljs$lang$macro = true;

var ret__18490__auto___7869 = (function (){
cljs.core$macros.vector = (function cljs$core$macros$vector(var_args){
var G__7868 = arguments.length;
switch (G__7868) {
case 2:
return cljs.core$macros.vector.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7871 = arguments.length;
var i__18451__auto___7872 = (0);
while(true){
if((i__18451__auto___7872 < len__18450__auto___7871)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7872]));

var G__7873 = (i__18451__auto___7872 + (1));
i__18451__auto___7872 = G__7873;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((2)),(0),null));
return cljs.core$macros.vector.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18470__auto__);

}
});

cljs.core$macros.vector.cljs$core$IFn$_invoke$arity$2 = (function (_AMPERSAND_form,_AMPERSAND_env){
return cljs.core.list(new cljs.core.Symbol(null,".-EMPTY",".-EMPTY",-471586691,null),new cljs.core.Symbol("cljs.core","PersistentVector","cljs.core/PersistentVector",-1211028272,null));
});

cljs.core$macros.vector.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,xs){
var cnt = cljs.core.count(xs);
if((cnt < (32))){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"PersistentVector.","PersistentVector.",-1758347237,null)),cljs.core._conj(cljs.core.List.EMPTY,null),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cnt;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,(5)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-EMPTY-NODE",".-EMPTY-NODE",-1333332641,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","PersistentVector","cljs.core/PersistentVector",-1211028272,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","array","cljs.core$macros/array",49650437,null)),xs)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
} else {
return cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".fromArray",".fromArray",1053499311,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","PersistentVector","cljs.core/PersistentVector",-1211028272,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","array","cljs.core$macros/array",49650437,null)),xs)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,true)], 0)))),cljs.core.assoc,new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol("cljs.core","PersistentVector","cljs.core/PersistentVector",-1211028272,null));
}
});

cljs.core$macros.vector.cljs$lang$applyTo = (function (seq7865){
var G__7866 = cljs.core.first(seq7865);
var seq7865__$1 = cljs.core.next(seq7865);
var G__7867 = cljs.core.first(seq7865__$1);
var seq7865__$2 = cljs.core.next(seq7865__$1);
return cljs.core$macros.vector.cljs$core$IFn$_invoke$arity$variadic(G__7866,G__7867,seq7865__$2);
});

cljs.core$macros.vector.cljs$lang$maxFixedArity = (2);

return null;
})()
;
cljs.core$macros.vector.cljs$lang$macro = true;

var ret__18490__auto___7883 = (function (){
cljs.core$macros.array_map = (function cljs$core$macros$array_map(var_args){
var G__7880 = arguments.length;
switch (G__7880) {
case 2:
return cljs.core$macros.array_map.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7885 = arguments.length;
var i__18451__auto___7886 = (0);
while(true){
if((i__18451__auto___7886 < len__18450__auto___7885)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7886]));

var G__7887 = (i__18451__auto___7886 + (1));
i__18451__auto___7886 = G__7887;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((2)),(0),null));
return cljs.core$macros.array_map.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18470__auto__);

}
});

cljs.core$macros.array_map.cljs$core$IFn$_invoke$arity$2 = (function (_AMPERSAND_form,_AMPERSAND_env){
return cljs.core.list(new cljs.core.Symbol(null,".-EMPTY",".-EMPTY",-471586691,null),new cljs.core.Symbol("cljs.core","PersistentArrayMap","cljs.core/PersistentArrayMap",1025194468,null));
});

cljs.core$macros.array_map.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,kvs){
var keys = cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.first,cljs.core.partition.cljs$core$IFn$_invoke$arity$2((2),kvs));
if((cljs.core.every_QMARK_(((function (keys){
return (function (p1__7874_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"op","op",-1882987955).cljs$core$IFn$_invoke$arity$1(p1__7874_SHARP_),new cljs.core.Keyword(null,"constant","constant",-379609303));
});})(keys))
,cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (keys){
return (function (p1__7875_SHARP_){
var _STAR_cljs_warnings_STAR_7882 = cljs.analyzer._STAR_cljs_warnings_STAR_;
cljs.analyzer._STAR_cljs_warnings_STAR_ = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"invoke-ctor","invoke-ctor",-756052704),new cljs.core.Keyword(null,"extending-base-js-type","extending-base-js-type",432787264),new cljs.core.Keyword(null,"unsupported-preprocess-value","unsupported-preprocess-value",1635147105),new cljs.core.Keyword(null,"redef","redef",1032704258),new cljs.core.Keyword(null,"js-shadowed-by-local","js-shadowed-by-local",198048291),new cljs.core.Keyword(null,"unsupported-js-module-type","unsupported-js-module-type",1806206180),new cljs.core.Keyword(null,"invalid-protocol-symbol","invalid-protocol-symbol",86246948),new cljs.core.Keyword(null,"protocol-duped-method","protocol-duped-method",15128166),new cljs.core.Keyword(null,"munged-namespace","munged-namespace",-101986199),new cljs.core.Keyword(null,"single-segment-namespace","single-segment-namespace",-491924469),new cljs.core.Keyword(null,"fn-arity","fn-arity",-403576501),new cljs.core.Keyword(null,"infer-warning","infer-warning",-1600671410),new cljs.core.Keyword(null,"variadic-max-arity","variadic-max-arity",-14288402),new cljs.core.Keyword(null,"protocol-with-variadic-method","protocol-with-variadic-method",-693368178),new cljs.core.Keyword(null,"fn-deprecated","fn-deprecated",626877647),new cljs.core.Keyword(null,"undeclared-var","undeclared-var",-1624364944),new cljs.core.Keyword(null,"preamble-missing","preamble-missing",220974801),new cljs.core.Keyword(null,"undeclared-protocol-symbol","undeclared-protocol-symbol",462882867),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"invalid-array-access","invalid-array-access",-1747132236),new cljs.core.Keyword(null,"unprovided","unprovided",-652330764),new cljs.core.Keyword(null,"ns-var-clash","ns-var-clash",-280494668),new cljs.core.Keyword(null,"undeclared-ns","undeclared-ns",-1589012812),new cljs.core.Keyword(null,"undeclared-ns-form","undeclared-ns-form",-248024137),new cljs.core.Keyword(null,"invalid-arithmetic","invalid-arithmetic",1917075065),new cljs.core.Keyword(null,"protocol-deprecated","protocol-deprecated",103233497),new cljs.core.Keyword(null,"fn-var","fn-var",1086204730),new cljs.core.Keyword(null,"protocol-impl-recur-with-target","protocol-impl-recur-with-target",-1648321574),new cljs.core.Keyword(null,"overload-arity","overload-arity",823206044),new cljs.core.Keyword(null,"protocol-multiple-impls","protocol-multiple-impls",794179260),new cljs.core.Keyword(null,"redef-in-file","redef-in-file",-476530788),new cljs.core.Keyword(null,"protocol-invalid-method","protocol-invalid-method",522647516),new cljs.core.Keyword(null,"extend-type-invalid-method-shape","extend-type-invalid-method-shape",1424103549),new cljs.core.Keyword(null,"multiple-variadic-overloads","multiple-variadic-overloads",1110059837),new cljs.core.Keyword(null,"protocol-impl-with-variadic-method","protocol-impl-with-variadic-method",-319321217)],[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false]);

try{return cljs.analyzer.analyze.cljs$core$IFn$_invoke$arity$2(_AMPERSAND_env,p1__7875_SHARP_);
}finally {cljs.analyzer._STAR_cljs_warnings_STAR_ = _STAR_cljs_warnings_STAR_7882;
}});})(keys))
,keys))) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cljs.core.count(cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentHashSet.EMPTY,keys)),cljs.core.count(keys)))){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"PersistentArrayMap.","PersistentArrayMap.",-1977657386,null)),cljs.core._conj(cljs.core.List.EMPTY,null),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = (cljs.core.count(kvs) / (2));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","array","cljs.core$macros/array",49650437,null)),kvs)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".createAsIfByAssoc",".createAsIfByAssoc",18554053,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","PersistentArrayMap","cljs.core/PersistentArrayMap",1025194468,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","array","cljs.core$macros/array",49650437,null)),kvs)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
}
});

cljs.core$macros.array_map.cljs$lang$applyTo = (function (seq7877){
var G__7878 = cljs.core.first(seq7877);
var seq7877__$1 = cljs.core.next(seq7877);
var G__7879 = cljs.core.first(seq7877__$1);
var seq7877__$2 = cljs.core.next(seq7877__$1);
return cljs.core$macros.array_map.cljs$core$IFn$_invoke$arity$variadic(G__7878,G__7879,seq7877__$2);
});

cljs.core$macros.array_map.cljs$lang$maxFixedArity = (2);

return null;
})()
;
cljs.core$macros.array_map.cljs$lang$macro = true;

var ret__18490__auto___7893 = (function (){
cljs.core$macros.hash_map = (function cljs$core$macros$hash_map(var_args){
var G__7892 = arguments.length;
switch (G__7892) {
case 2:
return cljs.core$macros.hash_map.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7895 = arguments.length;
var i__18451__auto___7896 = (0);
while(true){
if((i__18451__auto___7896 < len__18450__auto___7895)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7896]));

var G__7897 = (i__18451__auto___7896 + (1));
i__18451__auto___7896 = G__7897;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((2)),(0),null));
return cljs.core$macros.hash_map.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18470__auto__);

}
});

cljs.core$macros.hash_map.cljs$core$IFn$_invoke$arity$2 = (function (_AMPERSAND_form,_AMPERSAND_env){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-EMPTY",".-EMPTY",-471586691,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","PersistentHashMap","cljs.core/PersistentHashMap",-454120575,null)))));
});

cljs.core$macros.hash_map.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,kvs){
var pairs = cljs.core.partition.cljs$core$IFn$_invoke$arity$2((2),kvs);
var ks = cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.first,pairs);
var vs = cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.second,pairs);
return cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".fromArrays",".fromArrays",1110244209,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","PersistentHashMap","cljs.core/PersistentHashMap",-454120575,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","array","cljs.core$macros/array",49650437,null)),ks)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","array","cljs.core$macros/array",49650437,null)),vs)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),cljs.core.assoc,new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol("cljs.core","PersistentHashMap","cljs.core/PersistentHashMap",-454120575,null));
});

cljs.core$macros.hash_map.cljs$lang$applyTo = (function (seq7889){
var G__7890 = cljs.core.first(seq7889);
var seq7889__$1 = cljs.core.next(seq7889);
var G__7891 = cljs.core.first(seq7889__$1);
var seq7889__$2 = cljs.core.next(seq7889__$1);
return cljs.core$macros.hash_map.cljs$core$IFn$_invoke$arity$variadic(G__7890,G__7891,seq7889__$2);
});

cljs.core$macros.hash_map.cljs$lang$maxFixedArity = (2);

return null;
})()
;
cljs.core$macros.hash_map.cljs$lang$macro = true;

var ret__18490__auto___7907 = (function (){
cljs.core$macros.hash_set = (function cljs$core$macros$hash_set(var_args){
var G__7904 = arguments.length;
switch (G__7904) {
case 2:
return cljs.core$macros.hash_set.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
var args_arr__18469__auto__ = [];
var len__18450__auto___7909 = arguments.length;
var i__18451__auto___7910 = (0);
while(true){
if((i__18451__auto___7910 < len__18450__auto___7909)){
args_arr__18469__auto__.push((arguments[i__18451__auto___7910]));

var G__7911 = (i__18451__auto___7910 + (1));
i__18451__auto___7910 = G__7911;
continue;
} else {
}
break;
}

var argseq__18470__auto__ = (new cljs.core.IndexedSeq(args_arr__18469__auto__.slice((2)),(0),null));
return cljs.core$macros.hash_set.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18470__auto__);

}
});

cljs.core$macros.hash_set.cljs$core$IFn$_invoke$arity$2 = (function (_AMPERSAND_form,_AMPERSAND_env){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-EMPTY",".-EMPTY",-471586691,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","PersistentHashSet","cljs.core/PersistentHashSet",-967232330,null)))));
});

cljs.core$macros.hash_set.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,xs){
if(((cljs.core.count(xs) <= (8))) && (cljs.core.every_QMARK_((function (p1__7898_SHARP_){
return cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Keyword(null,"op","op",-1882987955).cljs$core$IFn$_invoke$arity$1(p1__7898_SHARP_),new cljs.core.Keyword(null,"constant","constant",-379609303));
}),cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__7899_SHARP_){
var _STAR_cljs_warnings_STAR_7906 = cljs.analyzer._STAR_cljs_warnings_STAR_;
cljs.analyzer._STAR_cljs_warnings_STAR_ = cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"invoke-ctor","invoke-ctor",-756052704),new cljs.core.Keyword(null,"extending-base-js-type","extending-base-js-type",432787264),new cljs.core.Keyword(null,"unsupported-preprocess-value","unsupported-preprocess-value",1635147105),new cljs.core.Keyword(null,"redef","redef",1032704258),new cljs.core.Keyword(null,"js-shadowed-by-local","js-shadowed-by-local",198048291),new cljs.core.Keyword(null,"unsupported-js-module-type","unsupported-js-module-type",1806206180),new cljs.core.Keyword(null,"invalid-protocol-symbol","invalid-protocol-symbol",86246948),new cljs.core.Keyword(null,"protocol-duped-method","protocol-duped-method",15128166),new cljs.core.Keyword(null,"munged-namespace","munged-namespace",-101986199),new cljs.core.Keyword(null,"single-segment-namespace","single-segment-namespace",-491924469),new cljs.core.Keyword(null,"fn-arity","fn-arity",-403576501),new cljs.core.Keyword(null,"infer-warning","infer-warning",-1600671410),new cljs.core.Keyword(null,"variadic-max-arity","variadic-max-arity",-14288402),new cljs.core.Keyword(null,"protocol-with-variadic-method","protocol-with-variadic-method",-693368178),new cljs.core.Keyword(null,"fn-deprecated","fn-deprecated",626877647),new cljs.core.Keyword(null,"undeclared-var","undeclared-var",-1624364944),new cljs.core.Keyword(null,"preamble-missing","preamble-missing",220974801),new cljs.core.Keyword(null,"undeclared-protocol-symbol","undeclared-protocol-symbol",462882867),new cljs.core.Keyword(null,"dynamic","dynamic",704819571),new cljs.core.Keyword(null,"invalid-array-access","invalid-array-access",-1747132236),new cljs.core.Keyword(null,"unprovided","unprovided",-652330764),new cljs.core.Keyword(null,"ns-var-clash","ns-var-clash",-280494668),new cljs.core.Keyword(null,"undeclared-ns","undeclared-ns",-1589012812),new cljs.core.Keyword(null,"undeclared-ns-form","undeclared-ns-form",-248024137),new cljs.core.Keyword(null,"invalid-arithmetic","invalid-arithmetic",1917075065),new cljs.core.Keyword(null,"protocol-deprecated","protocol-deprecated",103233497),new cljs.core.Keyword(null,"fn-var","fn-var",1086204730),new cljs.core.Keyword(null,"protocol-impl-recur-with-target","protocol-impl-recur-with-target",-1648321574),new cljs.core.Keyword(null,"overload-arity","overload-arity",823206044),new cljs.core.Keyword(null,"protocol-multiple-impls","protocol-multiple-impls",794179260),new cljs.core.Keyword(null,"redef-in-file","redef-in-file",-476530788),new cljs.core.Keyword(null,"protocol-invalid-method","protocol-invalid-method",522647516),new cljs.core.Keyword(null,"extend-type-invalid-method-shape","extend-type-invalid-method-shape",1424103549),new cljs.core.Keyword(null,"multiple-variadic-overloads","multiple-variadic-overloads",1110059837),new cljs.core.Keyword(null,"protocol-impl-with-variadic-method","protocol-impl-with-variadic-method",-319321217)],[false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false]);

try{return cljs.analyzer.analyze.cljs$core$IFn$_invoke$arity$2(_AMPERSAND_env,p1__7899_SHARP_);
}finally {cljs.analyzer._STAR_cljs_warnings_STAR_ = _STAR_cljs_warnings_STAR_7906;
}}),xs))) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cljs.core.count(cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentHashSet.EMPTY,xs)),cljs.core.count(xs)))){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"PersistentHashSet.","PersistentHashSet.",1210353164,null)),cljs.core._conj(cljs.core.List.EMPTY,null),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"PersistentArrayMap.","PersistentArrayMap.",-1977657386,null)),cljs.core._conj(cljs.core.List.EMPTY,null),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.count(xs);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","array","cljs.core$macros/array",49650437,null)),cljs.core.interleave.cljs$core$IFn$_invoke$arity$2(xs,cljs.core.repeat.cljs$core$IFn$_invoke$arity$1(null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
} else {
return cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".createAsIfByAssoc",".createAsIfByAssoc",18554053,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","PersistentHashSet","cljs.core/PersistentHashSet",-967232330,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","array","cljs.core$macros/array",49650437,null)),xs)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))),cljs.core.assoc,new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol("cljs.core","PersistentHashSet","cljs.core/PersistentHashSet",-967232330,null));
}
});

cljs.core$macros.hash_set.cljs$lang$applyTo = (function (seq7901){
var G__7902 = cljs.core.first(seq7901);
var seq7901__$1 = cljs.core.next(seq7901);
var G__7903 = cljs.core.first(seq7901__$1);
var seq7901__$2 = cljs.core.next(seq7901__$1);
return cljs.core$macros.hash_set.cljs$core$IFn$_invoke$arity$variadic(G__7902,G__7903,seq7901__$2);
});

cljs.core$macros.hash_set.cljs$lang$maxFixedArity = (2);

return null;
})()
;
cljs.core$macros.hash_set.cljs$lang$macro = true;

cljs.core$macros.js_obj_STAR_ = (function cljs$core$macros$js_obj_STAR_(kvs){
var kvs_str = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.str,cljs.core.interpose.cljs$core$IFn$_invoke$arity$2(",",cljs.core.take.cljs$core$IFn$_invoke$arity$2(cljs.core.count(kvs),cljs.core.repeat.cljs$core$IFn$_invoke$arity$1("~{}:~{}"))));
return cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(cljs.core.list_STAR_.cljs$core$IFn$_invoke$arity$3(new cljs.core.Symbol(null,"js*","js*",-1134233646,null),["{",cljs.core.str.cljs$core$IFn$_invoke$arity$1(kvs_str),"}"].join(''),cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.concat,kvs)),cljs.core.assoc,new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol(null,"object","object",-1179821820,null));
});
var ret__18490__auto___7927 = (function (){
cljs.core$macros.js_obj = (function cljs$core$macros$js_obj(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7928 = arguments.length;
var i__18451__auto___7929 = (0);
while(true){
if((i__18451__auto___7929 < len__18450__auto___7928)){
args__18453__auto__.push((arguments[i__18451__auto___7929]));

var G__7930 = (i__18451__auto___7929 + (1));
i__18451__auto___7929 = G__7930;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.js_obj.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.js_obj.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,rest){
var sym_or_str_QMARK_ = (function (x){
return ((x instanceof cljs.core.Symbol)) || (typeof x === 'string');
});
var filter_on_keys = ((function (sym_or_str_QMARK_){
return (function (f,coll){
return cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,cljs.core.filter.cljs$core$IFn$_invoke$arity$2(((function (sym_or_str_QMARK_){
return (function (p__7915){
var vec__7916 = p__7915;
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7916,(0),null);
var _ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7916,(1),null);
return (f.cljs$core$IFn$_invoke$arity$1 ? f.cljs$core$IFn$_invoke$arity$1(k) : f.call(null,k));
});})(sym_or_str_QMARK_))
,coll));
});})(sym_or_str_QMARK_))
;
var kvs = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentArrayMap.EMPTY,cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.vec,cljs.core.partition.cljs$core$IFn$_invoke$arity$2((2),rest)));
var sym_pairs = filter_on_keys(cljs.core.symbol_QMARK_,kvs);
var expr__GT_local = cljs.core.zipmap(cljs.core.filter.cljs$core$IFn$_invoke$arity$2(cljs.core.complement(sym_or_str_QMARK_),cljs.core.keys(kvs)),cljs.core.repeatedly.cljs$core$IFn$_invoke$arity$1(cljs.core.gensym));
var obj = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("obj");
if(cljs.core.empty_QMARK_(rest)){
return cljs.core$macros.js_obj_STAR_(cljs.core.List.EMPTY);
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.concat,clojure.set.map_invert(expr__GT_local)),(function (){var x__18265__auto__ = obj;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core$macros.js_obj_STAR_(filter_on_keys(cljs.core.string_QMARK_,kvs));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (sym_or_str_QMARK_,filter_on_keys,kvs,sym_pairs,expr__GT_local,obj){
return (function (p__7919){
var vec__7920 = p__7919;
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7920,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7920,(1),null);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("goog.object","set","goog.object/set",-2123361230,null)),(function (){var x__18265__auto__ = obj;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = k;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = v;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});})(sym_or_str_QMARK_,filter_on_keys,kvs,sym_pairs,expr__GT_local,obj))
,sym_pairs),cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (sym_or_str_QMARK_,filter_on_keys,kvs,sym_pairs,expr__GT_local,obj){
return (function (p__7923){
var vec__7924 = p__7923;
var k = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7924,(0),null);
var v = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__7924,(1),null);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("goog.object","set","goog.object/set",-2123361230,null)),(function (){var x__18265__auto__ = obj;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = v;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.get.cljs$core$IFn$_invoke$arity$2(kvs,k);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});})(sym_or_str_QMARK_,filter_on_keys,kvs,sym_pairs,expr__GT_local,obj))
,expr__GT_local),(function (){var x__18265__auto__ = obj;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
}
});

cljs.core$macros.js_obj.cljs$lang$maxFixedArity = (2);

cljs.core$macros.js_obj.cljs$lang$applyTo = (function (seq7912){
var G__7913 = cljs.core.first(seq7912);
var seq7912__$1 = cljs.core.next(seq7912);
var G__7914 = cljs.core.first(seq7912__$1);
var seq7912__$2 = cljs.core.next(seq7912__$1);
return cljs.core$macros.js_obj.cljs$core$IFn$_invoke$arity$variadic(G__7913,G__7914,seq7912__$2);
});

return null;
})()
;
cljs.core$macros.js_obj.cljs$lang$macro = true;

var ret__18490__auto___7931 = cljs.core$macros.alength = (function cljs$core$macros$alength(_AMPERSAND_form,_AMPERSAND_env,a){
return cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = a;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),"~{}.length"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null)),cljs.core.assoc,new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol(null,"number","number",-1084057331,null));
});
cljs.core$macros.alength.cljs$lang$macro = true;

var ret__18490__auto___7934 = /**
 * Maps an expression across an array a, using an index named idx, and
 *   return value named ret, initialized to a clone of a, then setting
 *   each element of ret to the evaluation of expr, returning the new
 *   array ret.
 */
cljs.core$macros.amap = (function cljs$core$macros$amap(_AMPERSAND_form,_AMPERSAND_env,a,idx,ret,expr){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"a__7932__auto__","a__7932__auto__",796426069,null)),(function (){var x__18265__auto__ = a;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"l__7933__auto__","l__7933__auto__",522809532,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","alength","cljs.core$macros/alength",-683052937,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"a__7932__auto__","a__7932__auto__",796426069,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = ret;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","aclone","cljs.core/aclone",-758078968,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"a__7932__auto__","a__7932__auto__",796426069,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","loop","cljs.core$macros/loop",1731108390,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,(0))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","<","cljs.core$macros/<",371512596,null)),(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"l__7933__auto__","l__7933__auto__",522809532,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","aset","cljs.core$macros/aset",-693176374,null)),(function (){var x__18265__auto__ = ret;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"recur","recur",1202958259,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","inc","cljs.core$macros/inc",876629257,null)),(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = ret;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.amap.cljs$lang$macro = true;

var ret__18490__auto___7937 = /**
 * Reduces an expression across an array a, using an index named idx,
 *   and return value named ret, initialized to init, setting ret to the
 *   evaluation of expr at each step, returning ret.
 */
cljs.core$macros.areduce = (function cljs$core$macros$areduce(_AMPERSAND_form,_AMPERSAND_env,a,idx,ret,init,expr){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"a__7935__auto__","a__7935__auto__",752336904,null)),(function (){var x__18265__auto__ = a;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"l__7936__auto__","l__7936__auto__",-140066795,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","alength","cljs.core$macros/alength",-683052937,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"a__7935__auto__","a__7935__auto__",752336904,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","loop","cljs.core$macros/loop",1731108390,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,(0)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = ret;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = init;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","<","cljs.core$macros/<",371512596,null)),(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"l__7936__auto__","l__7936__auto__",-140066795,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"recur","recur",1202958259,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","inc","cljs.core$macros/inc",876629257,null)),(function (){var x__18265__auto__ = idx;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = ret;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.areduce.cljs$lang$macro = true;

var ret__18490__auto___7943 = (function (){
/**
 * bindings => name n
 * 
 *   Repeatedly executes body (presumably for side-effects) with name
 *   bound to integers from 0 through n-1.
 */
cljs.core$macros.dotimes = (function cljs$core$macros$dotimes(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7944 = arguments.length;
var i__18451__auto___7945 = (0);
while(true){
if((i__18451__auto___7945 < len__18450__auto___7944)){
args__18453__auto__.push((arguments[i__18451__auto___7945]));

var G__7946 = (i__18451__auto___7945 + (1));
i__18451__auto___7945 = G__7946;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.dotimes.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.dotimes.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,body){
var i = cljs.core.first(bindings);
var n = cljs.core.second(bindings);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"n__7938__auto__","n__7938__auto__",-1593905475,null)),(function (){var x__18265__auto__ = n;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","loop","cljs.core$macros/loop",1731108390,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = i;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,(0))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when","cljs.core$macros/when",328457725,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","<","cljs.core$macros/<",371512596,null)),(function (){var x__18265__auto__ = i;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"n__7938__auto__","n__7938__auto__",-1593905475,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body,(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"recur","recur",1202958259,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","inc","cljs.core$macros/inc",876629257,null)),(function (){var x__18265__auto__ = i;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.dotimes.cljs$lang$maxFixedArity = (3);

cljs.core$macros.dotimes.cljs$lang$applyTo = (function (seq7939){
var G__7940 = cljs.core.first(seq7939);
var seq7939__$1 = cljs.core.next(seq7939);
var G__7941 = cljs.core.first(seq7939__$1);
var seq7939__$2 = cljs.core.next(seq7939__$1);
var G__7942 = cljs.core.first(seq7939__$2);
var seq7939__$3 = cljs.core.next(seq7939__$2);
return cljs.core$macros.dotimes.cljs$core$IFn$_invoke$arity$variadic(G__7940,G__7941,G__7942,seq7939__$3);
});

return null;
})()
;
cljs.core$macros.dotimes.cljs$lang$macro = true;

/**
 * Throws an exception if the given option map contains keys not listed
 *   as valid, else returns nil.
 */
cljs.core$macros.check_valid_options = (function cljs$core$macros$check_valid_options(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7950 = arguments.length;
var i__18451__auto___7951 = (0);
while(true){
if((i__18451__auto___7951 < len__18450__auto___7950)){
args__18453__auto__.push((arguments[i__18451__auto___7951]));

var G__7952 = (i__18451__auto___7951 + (1));
i__18451__auto___7951 = G__7952;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((1) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((1)),(0),null)):null);
return cljs.core$macros.check_valid_options.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__18454__auto__);
});

cljs.core$macros.check_valid_options.cljs$core$IFn$_invoke$arity$variadic = (function (options,valid_keys){
if(cljs.core.seq(cljs.core.apply.cljs$core$IFn$_invoke$arity$3(cljs.core.disj,cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.hash_set,cljs.core.keys(options)),valid_keys))){
throw cljs.core.apply.cljs$core$IFn$_invoke$arity$4(cljs.core.str,"Only these options are valid: ",cljs.core.first(valid_keys),cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__7947_SHARP_){
return [", ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(p1__7947_SHARP_)].join('');
}),cljs.core.rest(valid_keys)));
} else {
return null;
}
});

cljs.core$macros.check_valid_options.cljs$lang$maxFixedArity = (1);

cljs.core$macros.check_valid_options.cljs$lang$applyTo = (function (seq7948){
var G__7949 = cljs.core.first(seq7948);
var seq7948__$1 = cljs.core.next(seq7948);
return cljs.core$macros.check_valid_options.cljs$core$IFn$_invoke$arity$variadic(G__7949,seq7948__$1);
});

var ret__18490__auto___7962 = (function (){
/**
 * Creates a new multimethod with the associated dispatch function.
 *   The docstring and attribute-map are optional.
 * 
 *   Options are key-value pairs and may be one of:
 *  :default    the default dispatch value, defaults to :default
 *  :hierarchy  the isa? hierarchy to use for dispatching
 *              defaults to the global hierarchy
 */
cljs.core$macros.defmulti = (function cljs$core$macros$defmulti(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7963 = arguments.length;
var i__18451__auto___7964 = (0);
while(true){
if((i__18451__auto___7964 < len__18450__auto___7963)){
args__18453__auto__.push((arguments[i__18451__auto___7964]));

var G__7965 = (i__18451__auto___7964 + (1));
i__18451__auto___7964 = G__7965;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.defmulti.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.defmulti.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,mm_name,options){
var docstring = ((typeof cljs.core.first(options) === 'string')?cljs.core.first(options):null);
var options__$1 = ((typeof cljs.core.first(options) === 'string')?cljs.core.next(options):options);
var m = ((cljs.core.map_QMARK_(cljs.core.first(options__$1)))?cljs.core.first(options__$1):cljs.core.PersistentArrayMap.EMPTY);
var options__$2 = ((cljs.core.map_QMARK_(cljs.core.first(options__$1)))?cljs.core.next(options__$1):options__$1);
var dispatch_fn = cljs.core.first(options__$2);
var options__$3 = cljs.core.next(options__$2);
var m__$1 = (cljs.core.truth_(docstring)?cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(m,new cljs.core.Keyword(null,"doc","doc",1913296891),docstring):m);
var m__$2 = (cljs.core.truth_(cljs.core.meta(mm_name))?cljs.core.conj.cljs$core$IFn$_invoke$arity$2(cljs.core.meta(mm_name),m__$1):m__$1);
var mm_ns = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(_AMPERSAND_env)))].join('');
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cljs.core.count(options__$3),(1))){
throw (new Error("The syntax for defmulti has changed. Example: (defmulti name dispatch-fn :default dispatch-value)"));
} else {
}

var options__$4 = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.hash_map,options__$3);
var default$ = cljs.core.get.cljs$core$IFn$_invoke$arity$3(options__$4,new cljs.core.Keyword(null,"default","default",-1987822328),new cljs.core.Keyword(null,"default","default",-1987822328));
cljs.core$macros.check_valid_options.cljs$core$IFn$_invoke$arity$variadic(options__$4,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"default","default",-1987822328),new cljs.core.Keyword(null,"hierarchy","hierarchy",-1053470341)], 0));

return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","defonce","cljs.core$macros/defonce",-1096231613,null)),(function (){var x__18265__auto__ = cljs.core.with_meta(mm_name,m__$2);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"method-table__7953__auto__","method-table__7953__auto__",-814418705,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","atom","cljs.core$macros/atom",1728339282,null)),(function (){var x__18265__auto__ = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.array_map,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"prefer-table__7954__auto__","prefer-table__7954__auto__",834458392,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","atom","cljs.core$macros/atom",1728339282,null)),(function (){var x__18265__auto__ = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.array_map,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"method-cache__7955__auto__","method-cache__7955__auto__",1762634582,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","atom","cljs.core$macros/atom",1728339282,null)),(function (){var x__18265__auto__ = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.array_map,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"cached-hierarchy__7956__auto__","cached-hierarchy__7956__auto__",-572330553,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","atom","cljs.core$macros/atom",1728339282,null)),(function (){var x__18265__auto__ = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.array_map,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"hierarchy__7957__auto__","hierarchy__7957__auto__",-1693487485,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","get","cljs.core/get",-296075407,null)),(function (){var x__18265__auto__ = options__$4;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"hierarchy","hierarchy",-1053470341)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","get-global-hierarchy","cljs.core/get-global-hierarchy",48052871,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"MultiFn.","MultiFn.",1749135656,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","symbol","cljs.core/symbol",195265748,null)),(function (){var x__18265__auto__ = mm_ns;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.name(mm_name);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = dispatch_fn;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = default$;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"hierarchy__7957__auto__","hierarchy__7957__auto__",-1693487485,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"method-table__7953__auto__","method-table__7953__auto__",-814418705,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"prefer-table__7954__auto__","prefer-table__7954__auto__",834458392,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"method-cache__7955__auto__","method-cache__7955__auto__",1762634582,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"cached-hierarchy__7956__auto__","cached-hierarchy__7956__auto__",-572330553,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.defmulti.cljs$lang$maxFixedArity = (3);

cljs.core$macros.defmulti.cljs$lang$applyTo = (function (seq7958){
var G__7959 = cljs.core.first(seq7958);
var seq7958__$1 = cljs.core.next(seq7958);
var G__7960 = cljs.core.first(seq7958__$1);
var seq7958__$2 = cljs.core.next(seq7958__$1);
var G__7961 = cljs.core.first(seq7958__$2);
var seq7958__$3 = cljs.core.next(seq7958__$2);
return cljs.core$macros.defmulti.cljs$core$IFn$_invoke$arity$variadic(G__7959,G__7960,G__7961,seq7958__$3);
});

return null;
})()
;
cljs.core$macros.defmulti.cljs$lang$macro = true;

var ret__18490__auto___7971 = (function (){
/**
 * Creates and installs a new method of multimethod associated with dispatch-value. 
 */
cljs.core$macros.defmethod = (function cljs$core$macros$defmethod(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7972 = arguments.length;
var i__18451__auto___7973 = (0);
while(true){
if((i__18451__auto___7973 < len__18450__auto___7972)){
args__18453__auto__.push((arguments[i__18451__auto___7973]));

var G__7974 = (i__18451__auto___7973 + (1));
i__18451__auto___7973 = G__7974;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((4) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((4)),(0),null)):null);
return cljs.core$macros.defmethod.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18454__auto__);
});

cljs.core$macros.defmethod.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,multifn,dispatch_val,fn_tail){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-add-method","cljs.core$macros/-add-method",803359012,null)),(function (){var x__18265__auto__ = cljs.core.with_meta(multifn,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol("cljs.core","MultiFn","cljs.core/MultiFn",1487419554,null)], null));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = dispatch_val;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),fn_tail)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.defmethod.cljs$lang$maxFixedArity = (4);

cljs.core$macros.defmethod.cljs$lang$applyTo = (function (seq7966){
var G__7967 = cljs.core.first(seq7966);
var seq7966__$1 = cljs.core.next(seq7966);
var G__7968 = cljs.core.first(seq7966__$1);
var seq7966__$2 = cljs.core.next(seq7966__$1);
var G__7969 = cljs.core.first(seq7966__$2);
var seq7966__$3 = cljs.core.next(seq7966__$2);
var G__7970 = cljs.core.first(seq7966__$3);
var seq7966__$4 = cljs.core.next(seq7966__$3);
return cljs.core$macros.defmethod.cljs$core$IFn$_invoke$arity$variadic(G__7967,G__7968,G__7969,G__7970,seq7966__$4);
});

return null;
})()
;
cljs.core$macros.defmethod.cljs$lang$macro = true;

var ret__18490__auto___7977 = /**
 * Evaluates expr and prints the time it took. Returns the value of expr.
 */
cljs.core$macros.time = (function cljs$core$macros$time(_AMPERSAND_form,_AMPERSAND_env,expr){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"start__7975__auto__","start__7975__auto__",-148954622,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","system-time","cljs.core$macros/system-time",1102234543,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"ret__7976__auto__","ret__7976__auto__",2073870344,null)),(function (){var x__18265__auto__ = expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","prn","cljs.core$macros/prn",2053844509,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","str","cljs.core/str",-1971828991,null)),cljs.core._conj(cljs.core.List.EMPTY,"Elapsed time: "),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".toFixed",".toFixed",-895046938,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-","cljs.core$macros/-",13526976,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","system-time","cljs.core$macros/system-time",1102234543,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"start__7975__auto__","start__7975__auto__",-148954622,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,(6))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY," msecs")], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"ret__7976__auto__","ret__7976__auto__",2073870344,null))], 0))));
});
cljs.core$macros.time.cljs$lang$macro = true;

var ret__18490__auto___7992 = (function (){
/**
 * Runs expr iterations times in the context of a let expression with
 *   the given bindings, then prints out the bindings and the expr
 *   followed by number of iterations and total time. The optional
 *   argument print-fn, defaulting to println, sets function used to
 *   print the result. expr's string representation will be produced
 *   using pr-str in any case.
 */
cljs.core$macros.simple_benchmark = (function cljs$core$macros$simple_benchmark(var_args){
var args__18453__auto__ = [];
var len__18450__auto___7993 = arguments.length;
var i__18451__auto___7994 = (0);
while(true){
if((i__18451__auto___7994 < len__18450__auto___7993)){
args__18453__auto__.push((arguments[i__18451__auto___7994]));

var G__7995 = (i__18451__auto___7994 + (1));
i__18451__auto___7994 = G__7995;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((5) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((5)),(0),null)):null);
return cljs.core$macros.simple_benchmark.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),argseq__18454__auto__);
});

cljs.core$macros.simple_benchmark.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,bindings,expr,iterations,p__7989){
var map__7990 = p__7989;
var map__7990__$1 = ((((!((map__7990 == null)))?((((map__7990.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__7990.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.hash_map,map__7990):map__7990);
var print_fn = cljs.core.get.cljs$core$IFn$_invoke$arity$3(map__7990__$1,new cljs.core.Keyword(null,"print-fn","print-fn",-1720960489),new cljs.core.Symbol(null,"println","println",-733595439,null));
var bs_str = cljs.core.pr_str.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([bindings], 0));
var expr_str = cljs.core.pr_str.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([expr], 0));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = bindings;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"start__7978__auto__","start__7978__auto__",374648222,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".getTime",".getTime",-1048557777,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"Date.","Date.",384207886,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"ret__7979__auto__","ret__7979__auto__",681061473,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","dotimes","cljs.core$macros/dotimes",-1407597661,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"___7980__auto__","___7980__auto__",-720816672,null)),(function (){var x__18265__auto__ = iterations;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = expr;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"end__7981__auto__","end__7981__auto__",403596247,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".getTime",".getTime",-1048557777,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"Date.","Date.",384207886,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"elapsed__7982__auto__","elapsed__7982__auto__",-1213662424,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-","cljs.core$macros/-",13526976,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"end__7981__auto__","end__7981__auto__",403596247,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"start__7978__auto__","start__7978__auto__",374648222,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = print_fn;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","str","cljs.core$macros/str",-2019499702,null)),(function (){var x__18265__auto__ = bs_str;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,", "),(function (){var x__18265__auto__ = expr_str;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,", "),(function (){var x__18265__auto__ = iterations;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY," runs, "),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"elapsed__7982__auto__","elapsed__7982__auto__",-1213662424,null)),cljs.core._conj(cljs.core.List.EMPTY," msecs")], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.simple_benchmark.cljs$lang$maxFixedArity = (5);

cljs.core$macros.simple_benchmark.cljs$lang$applyTo = (function (seq7983){
var G__7984 = cljs.core.first(seq7983);
var seq7983__$1 = cljs.core.next(seq7983);
var G__7985 = cljs.core.first(seq7983__$1);
var seq7983__$2 = cljs.core.next(seq7983__$1);
var G__7986 = cljs.core.first(seq7983__$2);
var seq7983__$3 = cljs.core.next(seq7983__$2);
var G__7987 = cljs.core.first(seq7983__$3);
var seq7983__$4 = cljs.core.next(seq7983__$3);
var G__7988 = cljs.core.first(seq7983__$4);
var seq7983__$5 = cljs.core.next(seq7983__$4);
return cljs.core$macros.simple_benchmark.cljs$core$IFn$_invoke$arity$variadic(G__7984,G__7985,G__7986,G__7987,G__7988,seq7983__$5);
});

return null;
})()
;
cljs.core$macros.simple_benchmark.cljs$lang$macro = true;

cljs.core$macros.cs = cljs.core.into.cljs$core$IFn$_invoke$arity$2(cljs.core.PersistentVector.EMPTY,cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.comp.cljs$core$IFn$_invoke$arity$3(cljs.core.gensym,cljs.core.str,cljs.core.char$),cljs.core.range.cljs$core$IFn$_invoke$arity$2((97),(118))));
cljs.core$macros.gen_apply_to_helper = (function cljs$core$macros$gen_apply_to_helper(var_args){
var G__7997 = arguments.length;
switch (G__7997) {
case 0:
return cljs.core$macros.gen_apply_to_helper.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs.core$macros.gen_apply_to_helper.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core$macros.gen_apply_to_helper.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs.core$macros.gen_apply_to_helper.cljs$core$IFn$_invoke$arity$1((1));
});

cljs.core$macros.gen_apply_to_helper.cljs$core$IFn$_invoke$arity$1 = (function (n){
if((n <= (20))){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = (function (){var G__8001 = (n - (1));
return (cljs.core$macros.cs.cljs$core$IFn$_invoke$arity$1 ? cljs.core$macros.cs.cljs$core$IFn$_invoke$arity$1(G__8001) : cljs.core$macros.cs.call(null,G__8001));
})();
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-first","cljs.core$macros/-first",366553362,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"args","args",-1338879193,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"args","args",-1338879193,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-rest","cljs.core$macros/-rest",1731151371,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"args","args",-1338879193,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","==","cljs.core$macros/==",-818551413,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"argc","argc",187692008,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = n;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"f","f",43394975,null)),cljs.core.take.cljs$core$IFn$_invoke$arity$2(n,cljs.core$macros.cs))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core$macros.gen_apply_to_helper.cljs$core$IFn$_invoke$arity$1((n + (1)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"throw","throw",595905694,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"java.lang.Error.","java.lang.Error.",108378905,null)),cljs.core._conj(cljs.core.List.EMPTY,"Only up to 20 arguments supported on functions"))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
}
});

cljs.core$macros.gen_apply_to_helper.cljs$lang$maxFixedArity = 1;

var ret__18490__auto___8003 = cljs.core$macros.gen_apply_to = (function cljs$core$macros$gen_apply_to(_AMPERSAND_form,_AMPERSAND_env){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"*unchecked-if*","*unchecked-if*",1542408350,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,true)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","defn","cljs.core$macros/defn",-728332354,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"apply-to","apply-to",-1858571928,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"f","f",43394975,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"argc","argc",187692008,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"args","args",-1338879193,null))], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"args","args",-1338879193,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","seq","cljs.core$macros/seq",-1873195024,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"args","args",-1338879193,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","zero?","cljs.core$macros/zero?",-65998367,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"argc","argc",187692008,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"f","f",43394975,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core$macros.gen_apply_to_helper.cljs$core$IFn$_invoke$arity$0();
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"*unchecked-if*","*unchecked-if*",1542408350,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,false)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.gen_apply_to.cljs$lang$macro = true;

cljs.core$macros.gen_apply_to_simple_helper = (function cljs$core$macros$gen_apply_to_simple_helper(f,num_args,args){
var new_arg_sym = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["a",cljs.core.str.cljs$core$IFn$_invoke$arity$1(num_args)].join(''));
var proto_name = ["cljs$core$IFn$_invoke$arity$",cljs.core.str.cljs$core$IFn$_invoke$arity$1((num_args + (1)))].join('');
var proto_prop = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1([".-",cljs.core.str.cljs$core$IFn$_invoke$arity$1(proto_name)].join(''));
var proto_inv = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1([".",cljs.core.str.cljs$core$IFn$_invoke$arity$1(proto_name)].join(''));
var next_sym = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["next_",cljs.core.str.cljs$core$IFn$_invoke$arity$1(num_args)].join(''));
var all_args = cljs.core.mapv.cljs$core$IFn$_invoke$arity$2(((function (new_arg_sym,proto_name,proto_prop,proto_inv,next_sym){
return (function (p1__8004_SHARP_){
return cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["a",cljs.core.str.cljs$core$IFn$_invoke$arity$1(p1__8004_SHARP_)].join(''));
});})(new_arg_sym,proto_name,proto_prop,proto_inv,next_sym))
,cljs.core.range.cljs$core$IFn$_invoke$arity$1((num_args + (1))));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = new_arg_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","-first","cljs.core/-first",545297391,null)),(function (){var x__18265__auto__ = args;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = next_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","next","cljs.core/next",-1291438473,null)),(function (){var x__18265__auto__ = args;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","nil?","cljs.core$macros/nil?",83624258,null)),(function (){var x__18265__auto__ = next_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"if","if",1181717262,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = proto_prop;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = f;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = proto_inv;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = f;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([all_args], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".call",".call",1496874263,null)),(function (){var x__18265__auto__ = f;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = f;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),all_args], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = ((((19) <= num_args))?cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"arr__8005__auto__","arr__8005__auto__",1779453810,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","array","cljs.core/array",486685886,null)),all_args)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","loop","cljs.core$macros/loop",1731108390,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"s__8006__auto__","s__8006__auto__",-2123496126,null)),(function (){var x__18265__auto__ = next_sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when","cljs.core$macros/when",328457725,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"s__8006__auto__","s__8006__auto__",-2123496126,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".push",".push",-1497267248,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"arr__8005__auto__","arr__8005__auto__",1779453810,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","-first","cljs.core/-first",545297391,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"s__8006__auto__","s__8006__auto__",-2123496126,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"recur","recur",1202958259,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","next","cljs.core/next",-1291438473,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"s__8006__auto__","s__8006__auto__",-2123496126,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".apply",".apply",-1176201338,null)),(function (){var x__18265__auto__ = f;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = f;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"arr__8005__auto__","arr__8005__auto__",1779453810,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))):(function (){var G__8016 = f;
var G__8017 = (num_args + (1));
var G__8018 = next_sym;
return (cljs.core$macros.gen_apply_to_simple_helper.cljs$core$IFn$_invoke$arity$3 ? cljs.core$macros.gen_apply_to_simple_helper.cljs$core$IFn$_invoke$arity$3(G__8016,G__8017,G__8018) : cljs.core$macros.gen_apply_to_simple_helper.call(null,G__8016,G__8017,G__8018));
})());
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
var ret__18490__auto___8019 = cljs.core$macros.gen_apply_to_simple = (function cljs$core$macros$gen_apply_to_simple(_AMPERSAND_form,_AMPERSAND_env,f,num_args,args){
return cljs.core$macros.gen_apply_to_simple_helper(f,num_args,args);
});
cljs.core$macros.gen_apply_to_simple.cljs$lang$macro = true;

var ret__18490__auto___8025 = (function (){
/**
 * Evaluates exprs in a context in which *print-fn* is bound to .append
 *   on a fresh StringBuffer.  Returns the string created by any nested
 *   printing calls.
 */
cljs.core$macros.with_out_str = (function cljs$core$macros$with_out_str(var_args){
var args__18453__auto__ = [];
var len__18450__auto___8026 = arguments.length;
var i__18451__auto___8027 = (0);
while(true){
if((i__18451__auto___8027 < len__18450__auto___8026)){
args__18453__auto__.push((arguments[i__18451__auto___8027]));

var G__8028 = (i__18451__auto___8027 + (1));
i__18451__auto___8027 = G__8028;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.with_out_str.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.with_out_str.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,body){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"sb__8020__auto__","sb__8020__auto__",-421546586,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"goog.string.StringBuffer.","goog.string.StringBuffer.",-1043460809,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","binding","cljs.core$macros/binding",1855847304,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","*print-newline*","cljs.core/*print-newline*",6231625,null)),cljs.core._conj(cljs.core.List.EMPTY,true),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","*print-fn*","cljs.core/*print-fn*",1342365176,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__8021__auto__","x__8021__auto__",-1424264439,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".append",".append",1595439852,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"sb__8020__auto__","sb__8020__auto__",-421546586,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"x__8021__auto__","x__8021__auto__",-1424264439,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([body], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","str","cljs.core/str",-1971828991,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"sb__8020__auto__","sb__8020__auto__",-421546586,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.with_out_str.cljs$lang$maxFixedArity = (2);

cljs.core$macros.with_out_str.cljs$lang$applyTo = (function (seq8022){
var G__8023 = cljs.core.first(seq8022);
var seq8022__$1 = cljs.core.next(seq8022);
var G__8024 = cljs.core.first(seq8022__$1);
var seq8022__$2 = cljs.core.next(seq8022__$1);
return cljs.core$macros.with_out_str.cljs$core$IFn$_invoke$arity$variadic(G__8023,G__8024,seq8022__$2);
});

return null;
})()
;
cljs.core$macros.with_out_str.cljs$lang$macro = true;

var ret__18490__auto___8033 = (function (){
/**
 * Expands to code which yields a lazy sequence of the concatenation
 *   of the supplied colls.  Each coll expr is not evaluated until it is
 *   needed.
 * 
 *   (lazy-cat xs ys zs) === (concat (lazy-seq xs) (lazy-seq ys) (lazy-seq zs))
 */
cljs.core$macros.lazy_cat = (function cljs$core$macros$lazy_cat(var_args){
var args__18453__auto__ = [];
var len__18450__auto___8034 = arguments.length;
var i__18451__auto___8035 = (0);
while(true){
if((i__18451__auto___8035 < len__18450__auto___8034)){
args__18453__auto__.push((arguments[i__18451__auto___8035]));

var G__8036 = (i__18451__auto___8035 + (1));
i__18451__auto___8035 = G__8036;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.lazy_cat.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.lazy_cat.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,colls){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","concat","cljs.core$macros/concat",-2028571073,null)),cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__8029_SHARP_){
return cljs.core._conj((function (){var x__18265__auto__ = p1__8029_SHARP_;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),new cljs.core.Symbol("cljs.core$macros","lazy-seq","cljs.core$macros/lazy-seq",806482650,null));
}),colls))));
});

cljs.core$macros.lazy_cat.cljs$lang$maxFixedArity = (2);

cljs.core$macros.lazy_cat.cljs$lang$applyTo = (function (seq8030){
var G__8031 = cljs.core.first(seq8030);
var seq8030__$1 = cljs.core.next(seq8030);
var G__8032 = cljs.core.first(seq8030__$1);
var seq8030__$2 = cljs.core.next(seq8030__$1);
return cljs.core$macros.lazy_cat.cljs$core$IFn$_invoke$arity$variadic(G__8031,G__8032,seq8030__$2);
});

return null;
})()
;
cljs.core$macros.lazy_cat.cljs$lang$macro = true;

var ret__18490__auto___8037 = cljs.core$macros.js_str = (function cljs$core$macros$js_str(_AMPERSAND_form,_AMPERSAND_env,s){
return cljs.core._conj(cljs.core._conj((function (){var x__18265__auto__ = s;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),"''+~{}"),new cljs.core.Symbol(null,"js*","js*",-1134233646,null));
});
cljs.core$macros.js_str.cljs$lang$macro = true;

var ret__18490__auto___8039 = cljs.core$macros.es6_iterable = (function cljs$core$macros$es6_iterable(_AMPERSAND_form,_AMPERSAND_env,ty){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("goog.object","set","goog.object/set",-2123361230,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".-prototype",".-prototype",-1562038608,null)),(function (){var x__18265__auto__ = ty;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","ITER_SYMBOL","cljs.core/ITER_SYMBOL",-2091399233,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","this-as","cljs.core$macros/this-as",-799075148,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__8038__auto__","this__8038__auto__",-1545473910,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core","es6-iterator","cljs.core/es6-iterator",856007913,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"this__8038__auto__","this__8038__auto__",-1545473910,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.es6_iterable.cljs$lang$macro = true;

var ret__18490__auto___8056 = /**
 * Returns a map of the public intern mappings for the namespace.
 */
cljs.core$macros.ns_publics = (function cljs$core$macros$ns_publics(_AMPERSAND_form,_AMPERSAND_env,quoted_ns){
if((cljs.core.seq_QMARK_(quoted_ns)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cljs.core.first(quoted_ns),new cljs.core.Symbol(null,"quote","quote",1377916282,null))) && ((cljs.core.second(quoted_ns) instanceof cljs.core.Symbol))){
} else {
throw (new Error(["Assert failed: ","Argument to ns-publics must be a quoted symbol","\n","(core/and (seq? quoted-ns) (= (first quoted-ns) (quote quote)) (core/symbol? (second quoted-ns)))"].join('')));
}

var ns = cljs.core.second(quoted_ns);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","into","cljs.core$macros/into",1173696890,null)),(function (){var x__18265__auto__ = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.array_map,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (ns){
return (function (p__8048){
var vec__8049 = p__8048;
var sym = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__8049,(0),null);
var _ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__8049,(1),null);
return cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","symbol","cljs.core$macros/symbol",68939551,null)),(function (){var x__18265__auto__ = cljs.core.name(sym);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"var","var",870848730,null)),(function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$2(cljs.core.name(ns),cljs.core.name(sym));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
});})(ns))
,cljs.core.filter.cljs$core$IFn$_invoke$arity$2(((function (ns){
return (function (p__8052){
var vec__8053 = p__8052;
var _ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__8053,(0),null);
var info = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__8053,(1),null);
return cljs.core.not(new cljs.core.Keyword(null,"private","private",-558947994).cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"meta","meta",1499536964).cljs$core$IFn$_invoke$arity$1(info)));
});})(ns))
,cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(cljs.env._STAR_compiler_STAR_),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("cljs.analyzer","namespaces","cljs.analyzer/namespaces",-260788927),ns,new cljs.core.Keyword(null,"defs","defs",1398449717)], null))))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.ns_publics.cljs$lang$macro = true;

var ret__18490__auto___8065 = /**
 * Returns a map of the import mappings for the namespace.
 */
cljs.core$macros.ns_imports = (function cljs$core$macros$ns_imports(_AMPERSAND_form,_AMPERSAND_env,quoted_ns){
if((cljs.core.seq_QMARK_(quoted_ns)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cljs.core.first(quoted_ns),new cljs.core.Symbol(null,"quote","quote",1377916282,null))) && ((cljs.core.second(quoted_ns) instanceof cljs.core.Symbol))){
} else {
throw (new Error(["Assert failed: ","Argument to ns-imports must be a quoted symbol","\n","(core/and (seq? quoted-ns) (= (first quoted-ns) (quote quote)) (core/symbol? (second quoted-ns)))"].join('')));
}

var ns = cljs.core.second(quoted_ns);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","into","cljs.core$macros/into",1173696890,null)),(function (){var x__18265__auto__ = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.array_map,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (ns){
return (function (p__8061){
var vec__8062 = p__8061;
var ctor = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__8062,(0),null);
var qualified_ctor = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__8062,(1),null);
return cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","symbol","cljs.core$macros/symbol",68939551,null)),(function (){var x__18265__auto__ = cljs.core.name(ctor);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(qualified_ctor);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
});})(ns))
,cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(cljs.env._STAR_compiler_STAR_),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("cljs.analyzer","namespaces","cljs.analyzer/namespaces",-260788927),ns,new cljs.core.Keyword(null,"imports","imports",-1249933394)], null)))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.ns_imports.cljs$lang$macro = true;

var ret__18490__auto___8074 = /**
 * Returns a map of the intern mappings for the namespace.
 */
cljs.core$macros.ns_interns = (function cljs$core$macros$ns_interns(_AMPERSAND_form,_AMPERSAND_env,quoted_ns){
if((cljs.core.seq_QMARK_(quoted_ns)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cljs.core.first(quoted_ns),new cljs.core.Symbol(null,"quote","quote",1377916282,null))) && ((cljs.core.second(quoted_ns) instanceof cljs.core.Symbol))){
} else {
throw (new Error(["Assert failed: ","Argument to ns-interns must be a quoted symbol","\n","(core/and (seq? quoted-ns) (= (first quoted-ns) (quote quote)) (core/symbol? (second quoted-ns)))"].join('')));
}

var ns = cljs.core.second(quoted_ns);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","into","cljs.core$macros/into",1173696890,null)),(function (){var x__18265__auto__ = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.array_map,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (ns){
return (function (p__8070){
var vec__8071 = p__8070;
var sym = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__8071,(0),null);
var _ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__8071,(1),null);
return cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","symbol","cljs.core$macros/symbol",68939551,null)),(function (){var x__18265__auto__ = cljs.core.name(sym);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"var","var",870848730,null)),(function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$2(cljs.core.name(ns),cljs.core.name(sym));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
});})(ns))
,cljs.core.get_in.cljs$core$IFn$_invoke$arity$2(cljs.core.deref(cljs.env._STAR_compiler_STAR_),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("cljs.analyzer","namespaces","cljs.analyzer/namespaces",-260788927),ns,new cljs.core.Keyword(null,"defs","defs",1398449717)], null)))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.ns_interns.cljs$lang$macro = true;

var ret__18490__auto___8075 = /**
 * Removes the mappings for the symbol from the namespace.
 */
cljs.core$macros.ns_unmap = (function cljs$core$macros$ns_unmap(_AMPERSAND_form,_AMPERSAND_env,quoted_ns,quoted_sym){
if((cljs.core.seq_QMARK_(quoted_ns)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cljs.core.first(quoted_ns),new cljs.core.Symbol(null,"quote","quote",1377916282,null))) && ((cljs.core.second(quoted_ns) instanceof cljs.core.Symbol)) && (cljs.core.seq_QMARK_(quoted_sym)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cljs.core.first(quoted_sym),new cljs.core.Symbol(null,"quote","quote",1377916282,null))) && ((cljs.core.second(quoted_sym) instanceof cljs.core.Symbol))){
} else {
throw (new Error(["Assert failed: ","Arguments to ns-unmap must be quoted symbols","\n","(core/and (seq? quoted-ns) (= (first quoted-ns) (quote quote)) (core/symbol? (second quoted-ns)) (seq? quoted-sym) (= (first quoted-sym) (quote quote)) (core/symbol? (second quoted-sym)))"].join('')));
}

var ns = cljs.core.second(quoted_ns);
var sym = cljs.core.second(quoted_sym);
cljs.core.swap_BANG_.cljs$core$IFn$_invoke$arity$variadic(cljs.env._STAR_compiler_STAR_,cljs.core.update_in,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("cljs.analyzer","namespaces","cljs.analyzer/namespaces",-260788927),ns,new cljs.core.Keyword(null,"defs","defs",1398449717)], null),cljs.core.dissoc,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([sym], 0));

return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","js-delete","cljs.core$macros/js-delete",387769082,null)),(function (){var x__18265__auto__ = cljs.compiler.munge.cljs$core$IFn$_invoke$arity$1(ns);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.compiler.munge.cljs$core$IFn$_invoke$arity$1([cljs.core.str.cljs$core$IFn$_invoke$arity$1(sym)].join(''));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.ns_unmap.cljs$lang$macro = true;

var ret__18490__auto___8081 = (function (){
/**
 * Non-atomically swaps the value of the volatile as if:
 * (apply f current-value-of-vol args). Returns the value that
 * was swapped in.
 */
cljs.core$macros.vswap_BANG_ = (function cljs$core$macros$vswap_BANG_(var_args){
var args__18453__auto__ = [];
var len__18450__auto___8082 = arguments.length;
var i__18451__auto___8083 = (0);
while(true){
if((i__18451__auto___8083 < len__18450__auto___8082)){
args__18453__auto__.push((arguments[i__18451__auto___8083]));

var G__8084 = (i__18451__auto___8083 + (1));
i__18451__auto___8083 = G__8084;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((4) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((4)),(0),null)):null);
return cljs.core$macros.vswap_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),argseq__18454__auto__);
});

cljs.core$macros.vswap_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,vol,f,args){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-vreset!","cljs.core$macros/-vreset!",-1247881757,null)),(function (){var x__18265__auto__ = vol;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = f;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-deref","cljs.core$macros/-deref",-936303461,null)),(function (){var x__18265__auto__ = vol;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([args], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.vswap_BANG_.cljs$lang$maxFixedArity = (4);

cljs.core$macros.vswap_BANG_.cljs$lang$applyTo = (function (seq8076){
var G__8077 = cljs.core.first(seq8076);
var seq8076__$1 = cljs.core.next(seq8076);
var G__8078 = cljs.core.first(seq8076__$1);
var seq8076__$2 = cljs.core.next(seq8076__$1);
var G__8079 = cljs.core.first(seq8076__$2);
var seq8076__$3 = cljs.core.next(seq8076__$2);
var G__8080 = cljs.core.first(seq8076__$3);
var seq8076__$4 = cljs.core.next(seq8076__$3);
return cljs.core$macros.vswap_BANG_.cljs$core$IFn$_invoke$arity$variadic(G__8077,G__8078,G__8079,G__8080,seq8076__$4);
});

return null;
})()
;
cljs.core$macros.vswap_BANG_.cljs$lang$macro = true;

var ret__18490__auto___8089 = (function (){
cljs.core$macros.locking = (function cljs$core$macros$locking(var_args){
var args__18453__auto__ = [];
var len__18450__auto___8090 = arguments.length;
var i__18451__auto___8091 = (0);
while(true){
if((i__18451__auto___8091 < len__18450__auto___8090)){
args__18453__auto__.push((arguments[i__18451__auto___8091]));

var G__8092 = (i__18451__auto___8091 + (1));
i__18451__auto___8091 = G__8092;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.locking.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.locking.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,x,forms){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),forms)));
});

cljs.core$macros.locking.cljs$lang$maxFixedArity = (3);

cljs.core$macros.locking.cljs$lang$applyTo = (function (seq8085){
var G__8086 = cljs.core.first(seq8085);
var seq8085__$1 = cljs.core.next(seq8085);
var G__8087 = cljs.core.first(seq8085__$1);
var seq8085__$2 = cljs.core.next(seq8085__$1);
var G__8088 = cljs.core.first(seq8085__$2);
var seq8085__$3 = cljs.core.next(seq8085__$2);
return cljs.core$macros.locking.cljs$core$IFn$_invoke$arity$variadic(G__8086,G__8087,G__8088,seq8085__$3);
});

return null;
})()
;
cljs.core$macros.locking.cljs$lang$macro = true;

var ret__18490__auto___8096 = (function (){
/**
 * Loads libs, skipping any that are already loaded. Each argument is
 *   either a libspec that identifies a lib or a flag that modifies how all the identified
 *   libs are loaded. Use :require in the ns macro in preference to calling this
 *   directly.
 * 
 *   Libs
 * 
 *   A 'lib' is a named set of resources in classpath whose contents define a
 *   library of ClojureScript code. Lib names are symbols and each lib is associated
 *   with a ClojureScript namespace. A lib's name also locates its root directory
 *   within classpath using Java's package name to classpath-relative path mapping.
 *   All resources in a lib should be contained in the directory structure under its
 *   root directory. All definitions a lib makes should be in its associated namespace.
 * 
 *   'require loads a lib by loading its root resource. The root resource path
 *   is derived from the lib name in the following manner:
 *   Consider a lib named by the symbol 'x.y.z; it has the root directory
 *   <classpath>/x/y/, and its root resource is <classpath>/x/y/z.clj. The root
 *   resource should contain code to create the lib's namespace (usually by using
 *   the ns macro) and load any additional lib resources.
 * 
 *   Libspecs
 * 
 *   A libspec is a lib name or a vector containing a lib name followed by
 *   options expressed as sequential keywords and arguments.
 * 
 *   Recognized options:
 *   :as takes a symbol as its argument and makes that symbol an alias to the
 *  lib's namespace in the current namespace.
 *   :refer takes a list of symbols to refer from the namespace.
 *   :refer-macros takes a list of macro symbols to refer from the namespace.
 *   :include-macros true causes macros from the namespace to be required.
 *   :rename specifies a map from referred var names to different
 *  symbols (and can be used to prevent clashes)
 * 
 * 
 *   Flags
 * 
 *   A flag is a keyword.
 *   Recognized flags: :reload, :reload-all, :verbose
 *   :reload forces loading of all the identified libs even if they are
 *  already loaded
 *   :reload-all implies :reload and also forces loading of all libs that the
 *  identified libs directly or indirectly load via require or use
 *   :verbose triggers printing information about each load, alias, and refer
 * 
 *   Example:
 * 
 *   The following would load the library clojure.string :as string.
 * 
 *   (require '[clojure.string :as string])
 */
cljs.core$macros.require = (function cljs$core$macros$require(var_args){
var args__18453__auto__ = [];
var len__18450__auto___8097 = arguments.length;
var i__18451__auto___8098 = (0);
while(true){
if((i__18451__auto___8098 < len__18450__auto___8097)){
args__18453__auto__.push((arguments[i__18451__auto___8098]));

var G__8099 = (i__18451__auto___8098 + (1));
i__18451__auto___8098 = G__8099;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.require.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.require.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,args){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"ns*","ns*",1840949383,null)),(function (){var x__18265__auto__ = cljs.core.cons(new cljs.core.Keyword(null,"require","require",-468001333),args);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});

cljs.core$macros.require.cljs$lang$maxFixedArity = (2);

cljs.core$macros.require.cljs$lang$applyTo = (function (seq8093){
var G__8094 = cljs.core.first(seq8093);
var seq8093__$1 = cljs.core.next(seq8093);
var G__8095 = cljs.core.first(seq8093__$1);
var seq8093__$2 = cljs.core.next(seq8093__$1);
return cljs.core$macros.require.cljs$core$IFn$_invoke$arity$variadic(G__8094,G__8095,seq8093__$2);
});

return null;
})()
;
cljs.core$macros.require.cljs$lang$macro = true;

var ret__18490__auto___8103 = (function (){
/**
 * Similar to require but only for macros.
 */
cljs.core$macros.require_macros = (function cljs$core$macros$require_macros(var_args){
var args__18453__auto__ = [];
var len__18450__auto___8104 = arguments.length;
var i__18451__auto___8105 = (0);
while(true){
if((i__18451__auto___8105 < len__18450__auto___8104)){
args__18453__auto__.push((arguments[i__18451__auto___8105]));

var G__8106 = (i__18451__auto___8105 + (1));
i__18451__auto___8105 = G__8106;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.require_macros.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.require_macros.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,args){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"ns*","ns*",1840949383,null)),(function (){var x__18265__auto__ = cljs.core.cons(new cljs.core.Keyword(null,"require-macros","require-macros",707947416),args);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});

cljs.core$macros.require_macros.cljs$lang$maxFixedArity = (2);

cljs.core$macros.require_macros.cljs$lang$applyTo = (function (seq8100){
var G__8101 = cljs.core.first(seq8100);
var seq8100__$1 = cljs.core.next(seq8100);
var G__8102 = cljs.core.first(seq8100__$1);
var seq8100__$2 = cljs.core.next(seq8100__$1);
return cljs.core$macros.require_macros.cljs$core$IFn$_invoke$arity$variadic(G__8101,G__8102,seq8100__$2);
});

return null;
})()
;
cljs.core$macros.require_macros.cljs$lang$macro = true;

var ret__18490__auto___8110 = (function (){
/**
 * Like require, but referring vars specified by the mandatory
 *   :only option.
 * 
 *   Example:
 * 
 *   The following would load the library clojure.set while referring
 *   the intersection var.
 * 
 *   (use '[clojure.set :only [intersection]])
 */
cljs.core$macros.use = (function cljs$core$macros$use(var_args){
var args__18453__auto__ = [];
var len__18450__auto___8111 = arguments.length;
var i__18451__auto___8112 = (0);
while(true){
if((i__18451__auto___8112 < len__18450__auto___8111)){
args__18453__auto__.push((arguments[i__18451__auto___8112]));

var G__8113 = (i__18451__auto___8112 + (1));
i__18451__auto___8112 = G__8113;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.use.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.use.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,args){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"ns*","ns*",1840949383,null)),(function (){var x__18265__auto__ = cljs.core.cons(new cljs.core.Keyword(null,"use","use",-1846382424),args);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});

cljs.core$macros.use.cljs$lang$maxFixedArity = (2);

cljs.core$macros.use.cljs$lang$applyTo = (function (seq8107){
var G__8108 = cljs.core.first(seq8107);
var seq8107__$1 = cljs.core.next(seq8107);
var G__8109 = cljs.core.first(seq8107__$1);
var seq8107__$2 = cljs.core.next(seq8107__$1);
return cljs.core$macros.use.cljs$core$IFn$_invoke$arity$variadic(G__8108,G__8109,seq8107__$2);
});

return null;
})()
;
cljs.core$macros.use.cljs$lang$macro = true;

var ret__18490__auto___8117 = (function (){
/**
 * Similar to use but only for macros.
 */
cljs.core$macros.use_macros = (function cljs$core$macros$use_macros(var_args){
var args__18453__auto__ = [];
var len__18450__auto___8118 = arguments.length;
var i__18451__auto___8119 = (0);
while(true){
if((i__18451__auto___8119 < len__18450__auto___8118)){
args__18453__auto__.push((arguments[i__18451__auto___8119]));

var G__8120 = (i__18451__auto___8119 + (1));
i__18451__auto___8119 = G__8120;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.use_macros.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.use_macros.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,args){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"ns*","ns*",1840949383,null)),(function (){var x__18265__auto__ = cljs.core.cons(new cljs.core.Keyword(null,"use-macros","use-macros",-905638393),args);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});

cljs.core$macros.use_macros.cljs$lang$maxFixedArity = (2);

cljs.core$macros.use_macros.cljs$lang$applyTo = (function (seq8114){
var G__8115 = cljs.core.first(seq8114);
var seq8114__$1 = cljs.core.next(seq8114);
var G__8116 = cljs.core.first(seq8114__$1);
var seq8114__$2 = cljs.core.next(seq8114__$1);
return cljs.core$macros.use_macros.cljs$core$IFn$_invoke$arity$variadic(G__8115,G__8116,seq8114__$2);
});

return null;
})()
;
cljs.core$macros.use_macros.cljs$lang$macro = true;

var ret__18490__auto___8124 = (function (){
/**
 * import-list => (closure-namespace constructor-name-symbols*)
 * 
 *   For each name in constructor-name-symbols, adds a mapping from name to the
 *   constructor named by closure-namespace to the current namespace. Use :import in the ns
 *   macro in preference to calling this directly.
 */
cljs.core$macros.import$ = (function cljs$core$macros$import(var_args){
var args__18453__auto__ = [];
var len__18450__auto___8125 = arguments.length;
var i__18451__auto___8126 = (0);
while(true){
if((i__18451__auto___8126 < len__18450__auto___8125)){
args__18453__auto__.push((arguments[i__18451__auto___8126]));

var G__8127 = (i__18451__auto___8126 + (1));
i__18451__auto___8126 = G__8127;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.import$.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.import$.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,import_symbols_or_lists){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"ns*","ns*",1840949383,null)),(function (){var x__18265__auto__ = cljs.core.cons(new cljs.core.Keyword(null,"import","import",-1399500709),import_symbols_or_lists);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});

cljs.core$macros.import$.cljs$lang$maxFixedArity = (2);

cljs.core$macros.import$.cljs$lang$applyTo = (function (seq8121){
var G__8122 = cljs.core.first(seq8121);
var seq8121__$1 = cljs.core.next(seq8121);
var G__8123 = cljs.core.first(seq8121__$1);
var seq8121__$2 = cljs.core.next(seq8121__$1);
return cljs.core$macros.import$.cljs$core$IFn$_invoke$arity$variadic(G__8122,G__8123,seq8121__$2);
});

return null;
})()
;
cljs.core$macros.import$.cljs$lang$macro = true;

var ret__18490__auto___8131 = (function (){
/**
 * Refers to all the public vars of `cljs.core`, subject to
 *   filters.
 *   Filters can include at most one each of:
 * 
 *   :exclude list-of-symbols
 *   :rename map-of-fromsymbol-tosymbol
 * 
 *   Filters can be used to select a subset, via exclusion, or to provide a mapping
 *   to a symbol different from the var's name, in order to prevent clashes.
 */
cljs.core$macros.refer_clojure = (function cljs$core$macros$refer_clojure(var_args){
var args__18453__auto__ = [];
var len__18450__auto___8132 = arguments.length;
var i__18451__auto___8133 = (0);
while(true){
if((i__18451__auto___8133 < len__18450__auto___8132)){
args__18453__auto__.push((arguments[i__18451__auto___8133]));

var G__8134 = (i__18451__auto___8133 + (1));
i__18451__auto___8133 = G__8134;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((2) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((2)),(0),null)):null);
return cljs.core$macros.refer_clojure.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__18454__auto__);
});

cljs.core$macros.refer_clojure.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,args){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"ns*","ns*",1840949383,null)),(function (){var x__18265__auto__ = cljs.core.cons(new cljs.core.Keyword(null,"refer-clojure","refer-clojure",813784440),args);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
});

cljs.core$macros.refer_clojure.cljs$lang$maxFixedArity = (2);

cljs.core$macros.refer_clojure.cljs$lang$applyTo = (function (seq8128){
var G__8129 = cljs.core.first(seq8128);
var seq8128__$1 = cljs.core.next(seq8128);
var G__8130 = cljs.core.first(seq8128__$1);
var seq8128__$2 = cljs.core.next(seq8128__$1);
return cljs.core$macros.refer_clojure.cljs$core$IFn$_invoke$arity$variadic(G__8129,G__8130,seq8128__$2);
});

return null;
})()
;
cljs.core$macros.refer_clojure.cljs$lang$macro = true;

var ret__18490__auto___8135 = cljs.core$macros.load_file_STAR_ = (function cljs$core$macros$load_file_STAR_(_AMPERSAND_form,_AMPERSAND_env,f){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("js","goog","js/goog",-70605150,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"nodeGlobalRequire","nodeGlobalRequire",167018599,null)),(function (){var x__18265__auto__ = f;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.load_file_STAR_.cljs$lang$macro = true;

var ret__18490__auto___8136 = /**
 * If form represents a macro form, returns its expansion,
 *   else returns form.
 */
cljs.core$macros.macroexpand_1 = (function cljs$core$macros$macroexpand_1(_AMPERSAND_form,_AMPERSAND_env,quoted){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cljs.core.first(quoted),new cljs.core.Symbol(null,"quote","quote",1377916282,null))){
} else {
throw (new Error(["Assert failed: ","Argument to macroexpand-1 must be quoted","\n","(core/= (core/first quoted) (quote quote))"].join('')));
}

var form = cljs.core.second(quoted);
if(cljs.core.seq_QMARK_(form)){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"quote","quote",1377916282,null)),(function (){var x__18265__auto__ = cljs.analyzer.macroexpand_1(_AMPERSAND_env,form);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
} else {
return form;
}
});
cljs.core$macros.macroexpand_1.cljs$lang$macro = true;

var ret__18490__auto___8137 = /**
 * Repeatedly calls macroexpand-1 on form until it no longer
 *   represents a macro form, then returns it.  Note neither
 *   macroexpand-1 nor macroexpand expand macros in subforms.
 */
cljs.core$macros.macroexpand = (function cljs$core$macros$macroexpand(_AMPERSAND_form,_AMPERSAND_env,quoted){
if(cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cljs.core.first(quoted),new cljs.core.Symbol(null,"quote","quote",1377916282,null))){
} else {
throw (new Error(["Assert failed: ","Argument to macroexpand must be quoted","\n","(core/= (core/first quoted) (quote quote))"].join('')));
}

var form = cljs.core.second(quoted);
var env = _AMPERSAND_env;
if(cljs.core.seq_QMARK_(form)){
var form__$1 = form;
var form_SINGLEQUOTE_ = cljs.analyzer.macroexpand_1(env,form__$1);
while(true){
if(!((form__$1 === form_SINGLEQUOTE_))){
var G__8138 = form_SINGLEQUOTE_;
var G__8139 = cljs.analyzer.macroexpand_1(env,form_SINGLEQUOTE_);
form__$1 = G__8138;
form_SINGLEQUOTE_ = G__8139;
continue;
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"quote","quote",1377916282,null)),(function (){var x__18265__auto__ = form_SINGLEQUOTE_;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
}
break;
}
} else {
return form;
}
});
cljs.core$macros.macroexpand.cljs$lang$macro = true;

cljs.core$macros.multi_arity_fn_QMARK_ = (function cljs$core$macros$multi_arity_fn_QMARK_(fdecl){
return ((1) < cljs.core.count(fdecl));
});
cljs.core$macros.variadic_fn_QMARK_ = (function cljs$core$macros$variadic_fn_QMARK_(fdecl){
var and__17759__auto__ = cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2((1),cljs.core.count(fdecl));
if(and__17759__auto__){
return cljs.core.some(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Symbol(null,"&","&",-2144855648,null),null], null), null),cljs.core.ffirst(fdecl));
} else {
return and__17759__auto__;
}
});
cljs.core$macros.variadic_fn_STAR_ = (function cljs$core$macros$variadic_fn_STAR_(var_args){
var G__8141 = arguments.length;
switch (G__8141) {
case 2:
return cljs.core$macros.variadic_fn_STAR_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core$macros.variadic_fn_STAR_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core$macros.variadic_fn_STAR_.cljs$core$IFn$_invoke$arity$2 = (function (sym,method){
return cljs.core$macros.variadic_fn_STAR_.cljs$core$IFn$_invoke$arity$3(sym,method,true);
});

cljs.core$macros.variadic_fn_STAR_.cljs$core$IFn$_invoke$arity$3 = (function (sym,p__8142,solo){
var vec__8143 = p__8142;
var seq__8144 = cljs.core.seq(vec__8143);
var first__8145 = cljs.core.first(seq__8144);
var seq__8144__$1 = cljs.core.next(seq__8144);
var arglist = first__8145;
var body = seq__8144__$1;
var method = vec__8143;
var sig = cljs.core.remove.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Symbol(null,"&","&",-2144855648,null),null], null), null),arglist);
var restarg = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("seq");
var get_delegate = ((function (sig,restarg,vec__8143,seq__8144,first__8145,seq__8144__$1,arglist,body,method){
return (function cljs$core$macros$get_delegate(){
return new cljs.core.Symbol(null,"cljs$core$IFn$_invoke$arity$variadic","cljs$core$IFn$_invoke$arity$variadic",-378825034,null);
});})(sig,restarg,vec__8143,seq__8144,first__8145,seq__8144__$1,arglist,body,method))
;
var get_delegate_prop = ((function (sig,restarg,vec__8143,seq__8144,first__8145,seq__8144__$1,arglist,body,method){
return (function cljs$core$macros$get_delegate_prop(){
return cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["-",cljs.core.str.cljs$core$IFn$_invoke$arity$1(get_delegate())].join(''));
});})(sig,restarg,vec__8143,seq__8144,first__8145,seq__8144__$1,arglist,body,method))
;
var param_bind = ((function (sig,restarg,vec__8143,seq__8144,first__8145,seq__8144__$1,arglist,body,method){
return (function cljs$core$macros$param_bind(param){
return cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = param;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.with_meta(new cljs.core.Symbol("cljs.core$macros","first","cljs.core$macros/first",-999127023,null),cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.array_map,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"file","file",-1269645878)),cljs.core._conj(cljs.core.List.EMPTY,"cljs/core$macros.cljc"),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"line","line",212345235)),cljs.core._conj(cljs.core.List.EMPTY,2999),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"column","column",2078222095)),cljs.core._conj(cljs.core.List.EMPTY,49),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"end-line","end-line",1837326455)),cljs.core._conj(cljs.core.List.EMPTY,2999),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"end-column","end-column",1425389514)),cljs.core._conj(cljs.core.List.EMPTY,54),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword("cljs.analyzer","no-resolve","cljs.analyzer/no-resolve",-1872351017)),cljs.core._conj(cljs.core.List.EMPTY,true)], 0))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = restarg;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = restarg;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.with_meta(new cljs.core.Symbol("cljs.core$macros","next","cljs.core$macros/next",-1478604758,null),cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.array_map,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"file","file",-1269645878)),cljs.core._conj(cljs.core.List.EMPTY,"cljs/core$macros.cljc"),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"line","line",212345235)),cljs.core._conj(cljs.core.List.EMPTY,3000),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"column","column",2078222095)),cljs.core._conj(cljs.core.List.EMPTY,51),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"end-line","end-line",1837326455)),cljs.core._conj(cljs.core.List.EMPTY,3000),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"end-column","end-column",1425389514)),cljs.core._conj(cljs.core.List.EMPTY,55),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword("cljs.analyzer","no-resolve","cljs.analyzer/no-resolve",-1872351017)),cljs.core._conj(cljs.core.List.EMPTY,true)], 0))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = restarg;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))));
});})(sig,restarg,vec__8143,seq__8144,first__8145,seq__8144__$1,arglist,body,method))
;
var apply_to = ((function (sig,restarg,vec__8143,seq__8144,first__8145,seq__8144__$1,arglist,body,method){
return (function cljs$core$macros$apply_to(){
if(((1) < cljs.core.count(sig))){
var params = cljs.core.repeatedly.cljs$core$IFn$_invoke$arity$2((cljs.core.count(sig) - (1)),cljs.core.gensym);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1((function (){var x__18265__auto__ = restarg;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic(param_bind,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([params], 0))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic((function (){var x__18265__auto__ = get_delegate();
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),params,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = restarg;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1((function (){var x__18265__auto__ = restarg;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = get_delegate();
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","seq","cljs.core$macros/seq",-1873195024,null)),(function (){var x__18265__auto__ = restarg;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
}
});})(sig,restarg,vec__8143,seq__8144,first__8145,seq__8144__$1,arglist,body,method))
;
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = get_delegate_prop();
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.vec(sig);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),body)));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(cljs.core.truth_(solo)?cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1((function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-cljs$lang$maxFixedArity","-cljs$lang$maxFixedArity",-1481434279,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = (cljs.core.count(sig) - (1));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())))):null),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = sym;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-cljs$lang$applyTo","-cljs$lang$applyTo",-225535181,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = apply_to();
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});

cljs.core$macros.variadic_fn_STAR_.cljs$lang$maxFixedArity = 3;

var ret__18490__auto___8149 = cljs.core$macros.copy_arguments = (function cljs$core$macros$copy_arguments(_AMPERSAND_form,_AMPERSAND_env,dest){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"len__8147__auto__","len__8147__auto__",-1467239926,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","alength","cljs.core$macros/alength",-683052937,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","js-arguments","cljs.core$macros/js-arguments",390128540,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","loop","cljs.core$macros/loop",1731108390,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"i__8148__auto__","i__8148__auto__",-172928608,null)),cljs.core._conj(cljs.core.List.EMPTY,(0))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when","cljs.core$macros/when",328457725,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","<","cljs.core$macros/<",371512596,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"i__8148__auto__","i__8148__auto__",-172928608,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"len__8147__auto__","len__8147__auto__",-1467239926,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".push",".push",-1497267248,null)),(function (){var x__18265__auto__ = dest;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unchecked-get","cljs.core$macros/unchecked-get",-1185166893,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","js-arguments","cljs.core$macros/js-arguments",390128540,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"i__8148__auto__","i__8148__auto__",-172928608,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"recur","recur",1202958259,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","inc","cljs.core$macros/inc",876629257,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"i__8148__auto__","i__8148__auto__",-172928608,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.copy_arguments.cljs$lang$macro = true;

cljs.core$macros.variadic_fn = (function cljs$core$macros$variadic_fn(name,meta,p__8152,emit_var_QMARK_){
var vec__8153 = p__8152;
var vec__8156 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__8153,(0),null);
var seq__8157 = cljs.core.seq(vec__8156);
var first__8158 = cljs.core.first(seq__8157);
var seq__8157__$1 = cljs.core.next(seq__8157);
var arglist = first__8158;
var body = seq__8157__$1;
var method = vec__8156;
var fdecl = vec__8153;
var dest_args = ((function (vec__8153,vec__8156,seq__8157,first__8158,seq__8157__$1,arglist,body,method,fdecl){
return (function cljs$core$macros$variadic_fn_$_dest_args(c){
return cljs.core.map.cljs$core$IFn$_invoke$arity$2(((function (vec__8153,vec__8156,seq__8157,first__8158,seq__8157__$1,arglist,body,method,fdecl){
return (function (n){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unchecked-get","cljs.core$macros/unchecked-get",-1185166893,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","js-arguments","cljs.core$macros/js-arguments",390128540,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = n;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});})(vec__8153,vec__8156,seq__8157,first__8158,seq__8157__$1,arglist,body,method,fdecl))
,cljs.core.range.cljs$core$IFn$_invoke$arity$1(c));
});})(vec__8153,vec__8156,seq__8157,first__8158,seq__8157__$1,arglist,body,method,fdecl))
;
var rname = cljs.core.symbol.cljs$core$IFn$_invoke$arity$2([cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.analyzer._STAR_cljs_ns_STAR_)].join(''),[cljs.core.str.cljs$core$IFn$_invoke$arity$1(name)].join(''));
var sig = cljs.core.remove.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Symbol(null,"&","&",-2144855648,null),null], null), null),arglist);
var c_1 = (cljs.core.count(sig) - (1));
var meta__$1 = cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(meta,new cljs.core.Keyword(null,"top-fn","top-fn",-2056129173),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"variadic","variadic",882626057),true,new cljs.core.Keyword(null,"max-fixed-arity","max-fixed-arity",-690205543),c_1,new cljs.core.Keyword(null,"method-params","method-params",-980792179),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [sig], null),new cljs.core.Keyword(null,"arglists","arglists",1661989754),(function (){var x__18265__auto__ = arglist;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),new cljs.core.Keyword(null,"arglists-meta","arglists-meta",1944829838),cljs.core.doall.cljs$core$IFn$_invoke$arity$1(cljs.core.map.cljs$core$IFn$_invoke$arity$2(meta,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [arglist], null)))], null));
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"def","def",597100991,null)),(function (){var x__18265__auto__ = cljs.core.with_meta(name,meta__$1);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"var_args","var_args",1214280389,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"args__8150__auto__","args__8150__auto__",1624207137,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","array","cljs.core$macros/array",49650437,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","copy-arguments","cljs.core$macros/copy-arguments",-1675962356,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"args__8150__auto__","args__8150__auto__",1624207137,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"argseq__8151__auto__","argseq__8151__auto__",-796583625,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when","cljs.core$macros/when",328457725,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","<","cljs.core$macros/<",371512596,null)),(function (){var x__18265__auto__ = c_1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","alength","cljs.core$macros/alength",-683052937,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"args__8150__auto__","args__8150__auto__",1624207137,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"new","new",-444906321,null)),(function (){var x__18265__auto__ = cljs.core.with_meta(cljs.core.with_meta(new cljs.core.Symbol("cljs.core","IndexedSeq","cljs.core/IndexedSeq",-228688698,null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("cljs.analyzer","no-resolve","cljs.analyzer/no-resolve",-1872351017),true], null)),cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.array_map,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"file","file",-1269645878)),cljs.core._conj(cljs.core.List.EMPTY,"cljs/core$macros.cljc"),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"line","line",212345235)),cljs.core._conj(cljs.core.List.EMPTY,3047),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"column","column",2078222095)),cljs.core._conj(cljs.core.List.EMPTY,55),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"end-line","end-line",1837326455)),cljs.core._conj(cljs.core.List.EMPTY,3047),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"end-column","end-column",1425389514)),cljs.core._conj(cljs.core.List.EMPTY,75),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword("cljs.analyzer","no-resolve","cljs.analyzer/no-resolve",-1872351017)),cljs.core._conj(cljs.core.List.EMPTY,true)], 0))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".slice",".slice",1874048374,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"args__8150__auto__","args__8150__auto__",1624207137,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = c_1;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,(0)),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = rname;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"cljs$core$IFn$_invoke$arity$variadic","cljs$core$IFn$_invoke$arity$variadic",-378825034,null)),dest_args(c_1),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"argseq__8151__auto__","argseq__8151__auto__",-796583625,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core$macros.variadic_fn_STAR_.cljs$core$IFn$_invoke$arity$2(rname,method);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = (cljs.core.truth_(emit_var_QMARK_)?cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"var","var",870848730,null)),(function (){var x__18265__auto__ = name;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))):null);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.multi_arity_fn = (function cljs$core$macros$multi_arity_fn(name,meta,fdecl,emit_var_QMARK_){
var dest_args = (function cljs$core$macros$multi_arity_fn_$_dest_args(c){
return cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (n){
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","unchecked-get","cljs.core$macros/unchecked-get",-1185166893,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","js-arguments","cljs.core$macros/js-arguments",390128540,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = n;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
}),cljs.core.range.cljs$core$IFn$_invoke$arity$1(c));
});
var fixed_arity = (function cljs$core$macros$multi_arity_fn_$_fixed_arity(rname,sig){
var c = cljs.core.count(sig);
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [c,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = rname;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2((function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["cljs$core$IFn$_invoke$arity$",cljs.core.str.cljs$core$IFn$_invoke$arity$1(c)].join(''));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),dest_args(c))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))))], null);
});
var fn_method = (function cljs$core$macros$multi_arity_fn_$_fn_method(p__8167){
var vec__8168 = p__8167;
var seq__8169 = cljs.core.seq(vec__8168);
var first__8170 = cljs.core.first(seq__8169);
var seq__8169__$1 = cljs.core.next(seq__8169);
var sig = first__8170;
var body = seq__8169__$1;
var method = vec__8168;
if(cljs.core.truth_(cljs.core.some(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Symbol(null,"&","&",-2144855648,null),null], null), null),sig))){
return cljs.core$macros.variadic_fn_STAR_.cljs$core$IFn$_invoke$arity$3(name,method,false);
} else {
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = name;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.symbol.cljs$core$IFn$_invoke$arity$1(["-cljs$core$IFn$_invoke$arity$",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.count(sig))].join(''));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = method;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
}
});
var rname = cljs.core.symbol.cljs$core$IFn$_invoke$arity$2([cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.analyzer._STAR_cljs_ns_STAR_)].join(''),[cljs.core.str.cljs$core$IFn$_invoke$arity$1(name)].join(''));
var arglists = cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.first,fdecl);
var varsig_QMARK_ = ((function (rname,arglists){
return (function (p1__8159_SHARP_){
return cljs.core.some(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Symbol(null,"&","&",-2144855648,null),null], null), null),p1__8159_SHARP_);
});})(rname,arglists))
;
var variadic = cljs.core.boolean$(cljs.core.some(varsig_QMARK_,arglists));
var sigs = cljs.core.remove.cljs$core$IFn$_invoke$arity$2(varsig_QMARK_,arglists);
var maxfa = cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.max,cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.count,sigs),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(cljs.core.count(cljs.core.first(cljs.core.filter.cljs$core$IFn$_invoke$arity$2(varsig_QMARK_,arglists))) - (2))], null)));
var meta__$1 = cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(meta,new cljs.core.Keyword(null,"top-fn","top-fn",-2056129173),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"variadic","variadic",882626057),variadic,new cljs.core.Keyword(null,"max-fixed-arity","max-fixed-arity",-690205543),maxfa,new cljs.core.Keyword(null,"method-params","method-params",-980792179),sigs,new cljs.core.Keyword(null,"arglists","arglists",1661989754),arglists,new cljs.core.Keyword(null,"arglists-meta","arglists-meta",1944829838),cljs.core.doall.cljs$core$IFn$_invoke$arity$1(cljs.core.map.cljs$core$IFn$_invoke$arity$2(meta,arglists))], null));
var args_sym = cljs.core.gensym.cljs$core$IFn$_invoke$arity$1("args");
var param_counts = cljs.core.map.cljs$core$IFn$_invoke$arity$2(cljs.core.count,arglists);
if(cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(cljs.core.distinct.cljs$core$IFn$_invoke$arity$1(param_counts),param_counts)){
cljs.analyzer.warning(new cljs.core.Keyword(null,"overload-arity","overload-arity",823206044),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"name","name",1843675177),name], null));
} else {
}

return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"do","do",1686842252,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"def","def",597100991,null)),(function (){var x__18265__auto__ = cljs.core.with_meta(name,meta__$1);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"var_args","var_args",1214280389,null))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","case","cljs.core$macros/case",-2131866965,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","alength","cljs.core$macros/alength",-683052937,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","js-arguments","cljs.core$macros/js-arguments",390128540,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.mapcat.cljs$core$IFn$_invoke$arity$variadic(((function (rname,arglists,varsig_QMARK_,variadic,sigs,maxfa,meta__$1,args_sym,param_counts){
return (function (p1__8160_SHARP_){
return fixed_arity(rname,p1__8160_SHARP_);
});})(rname,arglists,varsig_QMARK_,variadic,sigs,maxfa,meta__$1,args_sym,param_counts))
,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([sigs], 0)),(function (){var x__18265__auto__ = ((variadic)?cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"args-arr__8161__auto__","args-arr__8161__auto__",-261770040,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","array","cljs.core$macros/array",49650437,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","copy-arguments","cljs.core$macros/copy-arguments",-1675962356,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"args-arr__8161__auto__","args-arr__8161__auto__",-261770040,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"argseq__8162__auto__","argseq__8162__auto__",-962732292,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"new","new",-444906321,null)),(function (){var x__18265__auto__ = cljs.core.with_meta(cljs.core.with_meta(new cljs.core.Symbol("cljs.core","IndexedSeq","cljs.core/IndexedSeq",-228688698,null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("cljs.analyzer","no-resolve","cljs.analyzer/no-resolve",-1872351017),true], null)),cljs.core.apply.cljs$core$IFn$_invoke$arity$2(cljs.core.array_map,cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"file","file",-1269645878)),cljs.core._conj(cljs.core.List.EMPTY,"cljs/core$macros.cljc"),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"line","line",212345235)),cljs.core._conj(cljs.core.List.EMPTY,3108),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"column","column",2078222095)),cljs.core._conj(cljs.core.List.EMPTY,59),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"end-line","end-line",1837326455)),cljs.core._conj(cljs.core.List.EMPTY,3108),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword(null,"end-column","end-column",1425389514)),cljs.core._conj(cljs.core.List.EMPTY,79),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Keyword("cljs.analyzer","no-resolve","cljs.analyzer/no-resolve",-1872351017)),cljs.core._conj(cljs.core.List.EMPTY,true)], 0))))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".slice",".slice",1874048374,null)),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"args-arr__8161__auto__","args-arr__8161__auto__",-261770040,null)),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = maxfa;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,(0)),cljs.core._conj(cljs.core.List.EMPTY,null)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = rname;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"cljs$core$IFn$_invoke$arity$variadic","cljs$core$IFn$_invoke$arity$variadic",-378825034,null)),dest_args(maxfa),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"argseq__8162__auto__","argseq__8162__auto__",-962732292,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0)))):(cljs.core.truth_(new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(meta__$1))?cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"throw","throw",595905694,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"java.lang.Error.","java.lang.Error.",108378905,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","str","cljs.core$macros/str",-2019499702,null)),cljs.core._conj(cljs.core.List.EMPTY,"Invalid arity: "),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","-","cljs.core$macros/-",13526976,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","alength","cljs.core$macros/alength",-683052937,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","js-arguments","cljs.core$macros/js-arguments",390128540,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,(2))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))):cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"throw","throw",595905694,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"java.lang.Error.","java.lang.Error.",108378905,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","str","cljs.core$macros/str",-2019499702,null)),cljs.core._conj(cljs.core.List.EMPTY,"Invalid arity: "),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","alength","cljs.core$macros/alength",-683052937,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$1(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","js-arguments","cljs.core$macros/js-arguments",390128540,null)))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core.map.cljs$core$IFn$_invoke$arity$2(fn_method,fdecl),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = name;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-cljs$lang$maxFixedArity","-cljs$lang$maxFixedArity",-1481434279,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = maxfa;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = (cljs.core.truth_(emit_var_QMARK_)?cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"var","var",870848730,null)),(function (){var x__18265__auto__ = name;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))):null);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
/**
 * Same as (def name (core/fn [params* ] exprs*)) or (def
 *  name (core/fn ([params* ] exprs*)+)) with any doc-string or attrs added
 *  to the var metadata. prepost-map defines a map with optional keys
 *  :pre and :post that contain collections of pre or post conditions.
 * @param {...*} var_args
 */
cljs.core$macros.defn = (function() { 
var cljs$core$macros$defn__delegate = function (_AMPERSAND_form,_AMPERSAND_env,name,fdecl){
if((name instanceof cljs.core.Symbol)){
} else {
throw (new Error("First argument to defn must be a symbol"));
}

var m = ((typeof cljs.core.first(fdecl) === 'string')?new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"doc","doc",1913296891),cljs.core.first(fdecl)], null):cljs.core.PersistentArrayMap.EMPTY);
var fdecl__$1 = ((typeof cljs.core.first(fdecl) === 'string')?cljs.core.next(fdecl):fdecl);
var m__$1 = ((cljs.core.map_QMARK_(cljs.core.first(fdecl__$1)))?cljs.core.conj.cljs$core$IFn$_invoke$arity$2(m,cljs.core.first(fdecl__$1)):m);
var fdecl__$2 = ((cljs.core.map_QMARK_(cljs.core.first(fdecl__$1)))?cljs.core.next(fdecl__$1):fdecl__$1);
var fdecl__$3 = ((cljs.core.vector_QMARK_(cljs.core.first(fdecl__$2)))?(function (){var x__18265__auto__ = fdecl__$2;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})():fdecl__$2);
var m__$2 = ((cljs.core.map_QMARK_(cljs.core.last(fdecl__$3)))?cljs.core.conj.cljs$core$IFn$_invoke$arity$2(m__$1,cljs.core.last(fdecl__$3)):m__$1);
var fdecl__$4 = ((cljs.core.map_QMARK_(cljs.core.last(fdecl__$3)))?cljs.core.butlast(fdecl__$3):fdecl__$3);
var m__$3 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"arglists","arglists",1661989754),cljs.core._conj((function (){var x__18265__auto__ = cljs.core$macros.sigs(fdecl__$4);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),new cljs.core.Symbol(null,"quote","quote",1377916282,null))], null),m__$2);
var m__$4 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2((cljs.core.truth_(cljs.core.meta(name))?cljs.core.meta(name):cljs.core.PersistentArrayMap.EMPTY),m__$3);
if(cljs.core.truth_(cljs.core$macros.multi_arity_fn_QMARK_(fdecl__$4))){
return cljs.core$macros.multi_arity_fn(name,(cljs.core.truth_(cljs.compiler.checking_types_QMARK_())?cljs.core.update_in.cljs$core$IFn$_invoke$arity$4(m__$4,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"jsdoc","jsdoc",1745183516)], null),cljs.core.conj,"@param {...*} var_args"):m__$4),fdecl__$4,new cljs.core.Keyword(null,"def-emits-var","def-emits-var",-1551927320).cljs$core$IFn$_invoke$arity$1(_AMPERSAND_env));
} else {
if(cljs.core.truth_(cljs.core$macros.variadic_fn_QMARK_(fdecl__$4))){
return cljs.core$macros.variadic_fn(name,(cljs.core.truth_(cljs.compiler.checking_types_QMARK_())?cljs.core.update_in.cljs$core$IFn$_invoke$arity$4(m__$4,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"jsdoc","jsdoc",1745183516)], null),cljs.core.conj,"@param {...*} var_args"):m__$4),fdecl__$4,new cljs.core.Keyword(null,"def-emits-var","def-emits-var",-1551927320).cljs$core$IFn$_invoke$arity$1(_AMPERSAND_env));
} else {
return cljs.core._conj((function (){var x__18265__auto__ = cljs.core.with_meta(name,m__$4);
return cljs.core._conj((function (){var x__18265__auto____$1 = cljs.core.cons(new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null),fdecl__$4);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto____$1);
})(),x__18265__auto__);
})(),new cljs.core.Symbol(null,"def","def",597100991,null));

}
}
};
var cljs$core$macros$defn = function (_AMPERSAND_form,_AMPERSAND_env,name,var_args){
var fdecl = null;
if (arguments.length > 3) {
var G__8171__i = 0, G__8171__a = new Array(arguments.length -  3);
while (G__8171__i < G__8171__a.length) {G__8171__a[G__8171__i] = arguments[G__8171__i + 3]; ++G__8171__i;}
  fdecl = new cljs.core.IndexedSeq(G__8171__a,0,null);
} 
return cljs$core$macros$defn__delegate.call(this,_AMPERSAND_form,_AMPERSAND_env,name,fdecl);};
cljs$core$macros$defn.cljs$lang$maxFixedArity = 3;
cljs$core$macros$defn.cljs$lang$applyTo = (function (arglist__8172){
var _AMPERSAND_form = cljs.core.first(arglist__8172);
arglist__8172 = cljs.core.next(arglist__8172);
var _AMPERSAND_env = cljs.core.first(arglist__8172);
arglist__8172 = cljs.core.next(arglist__8172);
var name = cljs.core.first(arglist__8172);
var fdecl = cljs.core.rest(arglist__8172);
return cljs$core$macros$defn__delegate(_AMPERSAND_form,_AMPERSAND_env,name,fdecl);
});
cljs$core$macros$defn.cljs$core$IFn$_invoke$arity$variadic = cljs$core$macros$defn__delegate;
return cljs$core$macros$defn;
})()
;
cljs.core$macros.defn.cljs$lang$macro = true;
/**
 * Like defn, but the resulting function name is declared as a
 *   macro and will be used as a macro by the compiler when it is
 *   called.
 */
cljs.core$macros.defmacro = (function cljs$core$macros$defmacro(var_args){
var args__18453__auto__ = [];
var len__18450__auto___8178 = arguments.length;
var i__18451__auto___8179 = (0);
while(true){
if((i__18451__auto___8179 < len__18450__auto___8178)){
args__18453__auto__.push((arguments[i__18451__auto___8179]));

var G__8180 = (i__18451__auto___8179 + (1));
i__18451__auto___8179 = G__8180;
continue;
} else {
}
break;
}

var argseq__18454__auto__ = ((((3) < args__18453__auto__.length))?(new cljs.core.IndexedSeq(args__18453__auto__.slice((3)),(0),null)):null);
return cljs.core$macros.defmacro.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__18454__auto__);
});

cljs.core$macros.defmacro.cljs$core$IFn$_invoke$arity$variadic = (function (_AMPERSAND_form,_AMPERSAND_env,name,args){
var prefix = (function (){var p = (function (){var x__18265__auto__ = cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(name,cljs.core.assoc,new cljs.core.Keyword(null,"macro","macro",-867863404),true);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})();
var args__$1 = args;
while(true){
var f = cljs.core.first(args__$1);
if(typeof f === 'string'){
var G__8181 = cljs.core.cons(f,p);
var G__8182 = cljs.core.next(args__$1);
p = G__8181;
args__$1 = G__8182;
continue;
} else {
if(cljs.core.map_QMARK_(f)){
var G__8183 = cljs.core.cons(f,p);
var G__8184 = cljs.core.next(args__$1);
p = G__8183;
args__$1 = G__8184;
continue;
} else {
return p;
}
}
break;
}
})();
var fdecl = (function (){var fd = args;
while(true){
if(typeof cljs.core.first(fd) === 'string'){
var G__8185 = cljs.core.next(fd);
fd = G__8185;
continue;
} else {
if(cljs.core.map_QMARK_(cljs.core.first(fd))){
var G__8186 = cljs.core.next(fd);
fd = G__8186;
continue;
} else {
return fd;
}
}
break;
}
})();
var fdecl__$1 = ((cljs.core.vector_QMARK_(cljs.core.first(fdecl)))?(function (){var x__18265__auto__ = fdecl;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})():fdecl);
var add_implicit_args = ((function (prefix,fdecl,fdecl__$1){
return (function (fd){
var args__$1 = cljs.core.first(fd);
return cljs.core.cons(cljs.core.vec(cljs.core.cons(new cljs.core.Symbol(null,"&form","&form",1482799337,null),cljs.core.cons(new cljs.core.Symbol(null,"&env","&env",-919163083,null),args__$1))),cljs.core.next(fd));
});})(prefix,fdecl,fdecl__$1))
;
var add_args = ((function (prefix,fdecl,fdecl__$1,add_implicit_args){
return (function (acc,ds){
while(true){
if((ds == null)){
return acc;
} else {
var d = cljs.core.first(ds);
if(cljs.core.map_QMARK_(d)){
return cljs.core.conj.cljs$core$IFn$_invoke$arity$2(acc,d);
} else {
var G__8187 = cljs.core.conj.cljs$core$IFn$_invoke$arity$2(acc,add_implicit_args(d));
var G__8188 = cljs.core.next(ds);
acc = G__8187;
ds = G__8188;
continue;
}
}
break;
}
});})(prefix,fdecl,fdecl__$1,add_implicit_args))
;
var fdecl__$2 = cljs.core.seq(add_args(cljs.core.PersistentVector.EMPTY,fdecl__$1));
var decl = (function (){var p = prefix;
var d = fdecl__$2;
while(true){
if(cljs.core.truth_(p)){
var G__8189 = cljs.core.next(p);
var G__8190 = cljs.core.cons(cljs.core.first(p),d);
p = G__8189;
d = G__8190;
continue;
} else {
return d;
}
break;
}
})();
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","let","cljs.core$macros/let",-160286726,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"ret__8173__auto__","ret__8173__auto__",2030976179,null)),(function (){var x__18265__auto__ = cljs.core.cons(new cljs.core.Symbol("cljs.core$macros","defn","cljs.core$macros/defn",-728332354,null),decl);
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"set!","set!",250714521,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,".",".",1975675962,null)),(function (){var x__18265__auto__ = name;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"-cljs$lang$macro","-cljs$lang$macro",443600924,null))], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([cljs.core._conj(cljs.core.List.EMPTY,true)], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"ret__8173__auto__","ret__8173__auto__",2030976179,null))], 0))));
});

cljs.core$macros.defmacro.cljs$lang$maxFixedArity = (3);

cljs.core$macros.defmacro.cljs$lang$applyTo = (function (seq8174){
var G__8175 = cljs.core.first(seq8174);
var seq8174__$1 = cljs.core.next(seq8174);
var G__8176 = cljs.core.first(seq8174__$1);
var seq8174__$2 = cljs.core.next(seq8174__$1);
var G__8177 = cljs.core.first(seq8174__$2);
var seq8174__$3 = cljs.core.next(seq8174__$2);
return cljs.core$macros.defmacro.cljs$core$IFn$_invoke$arity$variadic(G__8175,G__8176,G__8177,seq8174__$3);
});

cljs.core$macros.defmacro.cljs$lang$macro = true;
var ret__18490__auto___8195 = /**
 * Returns the var to which a symbol will be resolved in the namespace else nil.
 */
cljs.core$macros.resolve = (function cljs$core$macros$resolve(_AMPERSAND_form,_AMPERSAND_env,quoted_sym){
if((cljs.core.seq_QMARK_(quoted_sym)) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.first(quoted_sym)))){
} else {
throw (new Error(["Assert failed: ","Argument to resolve must be a quoted symbol","\n","(core/and (seq? quoted-sym) (= (quote quote) (first quoted-sym)))"].join('')));
}

var sym = cljs.core.second(quoted_sym);
var env = _AMPERSAND_env;
var vec__8191 = (function (){try{var var$ = cljs.analyzer.resolve_var.cljs$core$IFn$_invoke$arity$3(env,sym,cljs.analyzer.confirm_var_exists_throw());
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [var$,cljs.analyzer.var_meta.cljs$core$IFn$_invoke$arity$1(var$)], null);
}catch (e8194){var e = e8194;
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.analyzer.resolve_var.cljs$core$IFn$_invoke$arity$2(env,sym),null], null);
}})();
var var$ = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__8191,(0),null);
var meta = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__8191,(1),null);
var resolved = cljs.core.vary_meta.cljs$core$IFn$_invoke$arity$4(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(var$),cljs.core.assoc,new cljs.core.Keyword("cljs.analyzer","no-resolve","cljs.analyzer/no-resolve",-1872351017),true);
return cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","when","cljs.core$macros/when",328457725,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","exists?","cljs.core$macros/exists?",-1828590389,null)),(function (){var x__18265__auto__ = resolved;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"Var.","Var.",-640988922,null)),(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$variadic(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol("cljs.core$macros","fn","cljs.core$macros/fn",-187522821,null)),(function (){var x__18265__auto__ = cljs.core.vec(cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$0())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = resolved;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([(function (){var x__18265__auto__ = cljs.core.sequence.cljs$core$IFn$_invoke$arity$1(cljs.core.seq(cljs.core.concat.cljs$core$IFn$_invoke$arity$2(cljs.core._conj(cljs.core.List.EMPTY,new cljs.core.Symbol(null,"quote","quote",1377916282,null)),(function (){var x__18265__auto__ = resolved;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})())));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})(),(function (){var x__18265__auto__ = meta;
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
return cljs.core._conj(cljs.core.List.EMPTY,x__18265__auto__);
})()], 0))));
});
cljs.core$macros.resolve.cljs$lang$macro = true;

