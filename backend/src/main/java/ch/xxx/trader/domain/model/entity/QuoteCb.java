/**
 *    Copyright 2016 Sven Loesekann

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ch.xxx.trader.domain.model.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document
public class QuoteCb implements Quote {

	@Id
	private ObjectId _id;
	@Indexed
	@JsonProperty
	private Date createdAt = new Date();
	@JsonProperty("AED")
	private  BigDecimal aed;
	@JsonProperty("AFN")
	private  BigDecimal afn;
	@JsonProperty("ALL")
	private  BigDecimal all;
	@JsonProperty("AMD")
	private  BigDecimal amd;
	@JsonProperty("ANG")
	private  BigDecimal ang;
	@JsonProperty("AOA")
	private  BigDecimal aoa;
	@JsonProperty("ARS")
	private  BigDecimal ars;
	@JsonProperty("AUD")
	private  BigDecimal aud;
	@JsonProperty("AWG")
	private  BigDecimal awg;
	@JsonProperty("AZN")
	private  BigDecimal azn;
	@JsonProperty("BAM")
	private  BigDecimal bam;
	@JsonProperty("BBD")
	private  BigDecimal bbd;
	@JsonProperty("BDT")
	private  BigDecimal bdt;
	@JsonProperty("BGN")
	private  BigDecimal bgn;
	@JsonProperty("BHD")
	private  BigDecimal bhd;
	@JsonProperty("BIF")
	private  BigDecimal bif;
	@JsonProperty("BMD")
	private  BigDecimal bmd;
	@JsonProperty("BND")
	private  BigDecimal bnd;
	@JsonProperty("BOB")
	private  BigDecimal bob;
	@JsonProperty("BRL")
	private  BigDecimal brl;
	@JsonProperty("BSD")
	private  BigDecimal bsd;
	@JsonProperty("BTC")
	private  BigDecimal btc;
	@JsonProperty("BTN")
	private  BigDecimal btn;
	@JsonProperty("BWP")
	private  BigDecimal bwp;
	@JsonProperty("BYN")
	private  BigDecimal byn;
	@JsonProperty("BYR")
	private  BigDecimal byr;
	@JsonProperty("BZD")
	private  BigDecimal bzd;
	@JsonProperty("CAD")
	private  BigDecimal cad;
	@JsonProperty("CDF")
	private  BigDecimal cdf;
	@JsonProperty("CHF")
	private  BigDecimal chf;
	@JsonProperty("CLF")
	private  BigDecimal clf;
	@JsonProperty("CLP")
	private  BigDecimal clp;
	@JsonProperty("CNY")
	private  BigDecimal cny;
	@JsonProperty("COP")
	private  BigDecimal cop;
	@JsonProperty("CRC")
	private  BigDecimal crc;
	@JsonProperty("CUC")
	private  BigDecimal cuc;
	@JsonProperty("CVE")
	private  BigDecimal cve;
	@JsonProperty("CZK")
	private  BigDecimal czk;
	@JsonProperty("DJF")
	private  BigDecimal djf;
	@JsonProperty("DKK")
	private  BigDecimal dkk;
	@JsonProperty("DOP")
	private  BigDecimal dop;
	@JsonProperty("DZD")
	private  BigDecimal dzd;
	@JsonProperty("EEK")
	private  BigDecimal eek;
	@JsonProperty("EGP")
	private  BigDecimal egp;
	@JsonProperty("ERN")
	private  BigDecimal ern;
	@JsonProperty("ETB")
	private  BigDecimal etb;
	@JsonProperty("ETH")
	private  BigDecimal eth;
	@JsonProperty("EUR")
	private  BigDecimal eur;
	@JsonProperty("FJD")
	private  BigDecimal fjd;
	@JsonProperty("FKP")
	private  BigDecimal fkp;
	@JsonProperty("GBP")
	private  BigDecimal gbp;
	@JsonProperty("GEL")
	private  BigDecimal gel;
	@JsonProperty("GGP")
	private  BigDecimal ggp;
	@JsonProperty("GHS")
	private  BigDecimal ghs;
	@JsonProperty("GIP")
	private  BigDecimal gip;
	@JsonProperty("GMD")
	private  BigDecimal gmd;
	@JsonProperty("GNF")
	private  BigDecimal gnf;
	@JsonProperty("GTQ")
	private  BigDecimal gtq;
	@JsonProperty("GYD")
	private  BigDecimal gyd;
	@JsonProperty("HKD")
	private  BigDecimal hkd;
	@JsonProperty("HNL")
	private  BigDecimal hnl;
	@JsonProperty("HRK")
	private  BigDecimal hrk;
	@JsonProperty("HTG")
	private  BigDecimal htg;
	@JsonProperty("HUF")
	private  BigDecimal huf;
	@JsonProperty("IDR")
	private  BigDecimal idr;
	@JsonProperty("ILS")
	private  BigDecimal ils;
	@JsonProperty("IMP")
	private  BigDecimal imp;
	@JsonProperty("INR")
	private  BigDecimal inr;
	@JsonProperty("IQD")
	private  BigDecimal iqd;
	@JsonProperty("ISK")
	private  BigDecimal isk;
	@JsonProperty("JEP")
	private  BigDecimal jep;
	@JsonProperty("JMD")
	private  BigDecimal jmd;
	@JsonProperty("JOD")
	private  BigDecimal jod;
	@JsonProperty("JPY")
	private  BigDecimal jpy;
	@JsonProperty("KES")
	private  BigDecimal kes;
	@JsonProperty("KGS")
	private  BigDecimal kgs;
	@JsonProperty("KHR")
	private  BigDecimal khr;
	@JsonProperty("KMF")
	private  BigDecimal kmf;
	@JsonProperty("KRW")
	private  BigDecimal krw;
	@JsonProperty("KWD")
	private  BigDecimal kwd;
	@JsonProperty("KYD")
	private  BigDecimal kyd;
	@JsonProperty("KZT")
	private  BigDecimal kzt;
	@JsonProperty("LAK")
	private  BigDecimal lak;
	@JsonProperty("LBP")
	private  BigDecimal lbp;
	@JsonProperty("LKR")
	private  BigDecimal lkr;
	@JsonProperty("LRD")
	private  BigDecimal lrd;
	@JsonProperty("LSL")
	private  BigDecimal lsl;
	@JsonProperty("LTC")
	private  BigDecimal ltc;
	@JsonProperty("LTL")
	private  BigDecimal ltl;
	@JsonProperty("LVL")
	private  BigDecimal lvl;
	@JsonProperty("LYD")
	private  BigDecimal lyd;
	@JsonProperty("MAD")
	private  BigDecimal mad;
	@JsonProperty("MDL")
	private  BigDecimal mdl;
	@JsonProperty("MGA")
	private  BigDecimal mga;
	@JsonProperty("MKD")
	private  BigDecimal mkd;
	@JsonProperty("MMK")
	private  BigDecimal mmk;
	@JsonProperty("MNT")
	private  BigDecimal mnt;
	@JsonProperty("MOP")
	private  BigDecimal mop;
	@JsonProperty("MRO")
	private  BigDecimal mro;
	@JsonProperty("MTL")
	private  BigDecimal mtl;
	@JsonProperty("MUR")
	private  BigDecimal mur;
	@JsonProperty("MVR")
	private  BigDecimal mvr;
	@JsonProperty("MWK")
	private  BigDecimal mwk;
	@JsonProperty("MXN")
	private  BigDecimal mxn;
	@JsonProperty("MYR")
	private  BigDecimal myr;
	@JsonProperty("MZN")
	private  BigDecimal mzn;
	@JsonProperty("NAD")
	private  BigDecimal nad;
	@JsonProperty("NGN")
	private  BigDecimal ngn;
	@JsonProperty("NIO")
	private  BigDecimal nio;
	@JsonProperty("NOK")
	private  BigDecimal nok;
	@JsonProperty("NPR")
	private  BigDecimal npr;
	@JsonProperty("NZD")
	private  BigDecimal nzd;
	@JsonProperty("OMR")
	private  BigDecimal omr;
	@JsonProperty("PAB")
	private  BigDecimal pab;
	@JsonProperty("PEN")
	private  BigDecimal pen;
	@JsonProperty("PGK")
	private  BigDecimal pgk;
	@JsonProperty("PHP")
	private  BigDecimal php;
	@JsonProperty("PKR")
	private  BigDecimal pkr;
	@JsonProperty("PLN")
	private  BigDecimal pln;
	@JsonProperty("PYG")
	private  BigDecimal pyg;
	@JsonProperty("QAR")
	private  BigDecimal qar;
	@JsonProperty("RON")
	private  BigDecimal ron;
	@JsonProperty("RSD")
	private  BigDecimal rsd;
	@JsonProperty("RUB")
	private  BigDecimal rub;
	@JsonProperty("RWF")
	private  BigDecimal rwf;
	@JsonProperty("SAR")
	private  BigDecimal sar;
	@JsonProperty("SBD")
	private  BigDecimal sbd;
	@JsonProperty("SCR")
	private  BigDecimal scr;
	@JsonProperty("SEK")
	private  BigDecimal sek;
	@JsonProperty("SGD")
	private  BigDecimal sgd;
	@JsonProperty("SHP")
	private  BigDecimal shp;
	@JsonProperty("SLL")
	private  BigDecimal sll;
	@JsonProperty("SOS")
	private  BigDecimal sos;
	@JsonProperty("SRD")
	private  BigDecimal srd;
	@JsonProperty("SSP")
	private  BigDecimal ssp;
	@JsonProperty("STD")
	private  BigDecimal std;
	@JsonProperty("SVC")
	private  BigDecimal svc;
	@JsonProperty("SZL")
	private  BigDecimal szl;
	@JsonProperty("THB")
	private  BigDecimal thb;
	@JsonProperty("TJS")
	private  BigDecimal tjs;
	@JsonProperty("TMT")
	private  BigDecimal tmt;
	@JsonProperty("TND")
	private  BigDecimal tnd;
	@JsonProperty("TOP")
	private  BigDecimal top;	
	private  BigDecimal try1;
	@JsonProperty("TTD")
	private  BigDecimal ttd;
	@JsonProperty("TWD")
	private  BigDecimal twd;
	@JsonProperty("TZS")
	private  BigDecimal tzs;
	@JsonProperty("UAH")
	private  BigDecimal uah;
	@JsonProperty("UGX")
	private  BigDecimal ugx;
	@JsonProperty("USD")
	private  BigDecimal usd;
	@JsonProperty("UYU")
	private  BigDecimal uyu;
	@JsonProperty("UZS")
	private  BigDecimal uzs;
	@JsonProperty("VEF")
	private  BigDecimal vef;
	@JsonProperty("VND")
	private  BigDecimal vnd;
	@JsonProperty("VUV")
	private  BigDecimal vuv;
	@JsonProperty("WST")
	private  BigDecimal wst;
	@JsonProperty("XAF")
	private  BigDecimal xaf;
	@JsonProperty("XAG")
	private  BigDecimal xag;
	@JsonProperty("XAU")
	private  BigDecimal xau;
	@JsonProperty("XCD")
	private  BigDecimal xcd;
	@JsonProperty("XDR")
	private  BigDecimal xdr;
	@JsonProperty("XOF")
	private  BigDecimal xof;
	@JsonProperty("XPD")
	private  BigDecimal xpd;
	@JsonProperty("XPF")
	private  BigDecimal xpf;
	@JsonProperty("XPT")
	private  BigDecimal xpt;
	@JsonProperty("YER")
	private  BigDecimal yer;
	@JsonProperty("ZAR")
	private  BigDecimal zar;
	@JsonProperty("ZMK")
	private  BigDecimal zmk;
	@JsonProperty("ZMW")
	private  BigDecimal zmw;
	@JsonProperty("ZWL")
	private  BigDecimal zwl;
	@JsonProperty("VES")
	private  BigDecimal ves;
	@JsonProperty("XBA")
	private  BigDecimal xba;
	@JsonProperty("XTS")
	private  BigDecimal xts;
	@JsonProperty("GBX")
	private  BigDecimal gbx;
	@JsonProperty("CNH")
	private  BigDecimal cnh;
	@JsonProperty("ZWD")
	private  BigDecimal zwd;
	@JsonProperty("BCH")
	private  BigDecimal bch;
	@JsonProperty("BSV")
	private  BigDecimal bsv;
	@JsonProperty("ETH2")
	private  BigDecimal eth2;
	@JsonProperty("ETC")
	private  BigDecimal etc;
	@JsonProperty("ZRX")
	private  BigDecimal zrx;
	@JsonProperty("USDC")
	private  BigDecimal usdc;
	@JsonProperty("BAT")
	private  BigDecimal bat;
	@JsonProperty("LOOM")
	private  BigDecimal loom;
	@JsonProperty("MANA")
	private  BigDecimal mana;
	@JsonProperty("KNC")
	private  BigDecimal knc;
	@JsonProperty("LINK")
	private  BigDecimal link;
	@JsonProperty("MKR")
	private  BigDecimal mkr;
	@JsonProperty("CVC")
	private  BigDecimal cvc;
	@JsonProperty("OMG")
	private  BigDecimal omg;
	@JsonProperty("GNT")
	private  BigDecimal gnt;
	@JsonProperty("DAI")
	private  BigDecimal dai;
	@JsonProperty("SNT")
	private  BigDecimal snt;
	@JsonProperty("ZEC")
	private  BigDecimal zec;
	@JsonProperty("XRP")
	private  BigDecimal xrp;
	@JsonProperty("REP")
	private  BigDecimal rep;
	@JsonProperty("XLM")
	private  BigDecimal xlm;
	@JsonProperty("EOS")
	private  BigDecimal eos;
	@JsonProperty("XTZ")
	private  BigDecimal xtz;
	@JsonProperty("ALGO")
	private  BigDecimal algo;
	@JsonProperty("DASH")
	private  BigDecimal dash;
	@JsonProperty("ATOM")
	private  BigDecimal atom;
	@JsonProperty("OXT")
	private  BigDecimal oxt;
	@JsonProperty("COMP")
	private  BigDecimal comp;
	@JsonProperty("ENJ")
	private  BigDecimal enj;
	@JsonProperty("REPV2")
	private  BigDecimal repv2;
	@JsonProperty("BAND")
	private  BigDecimal band;
	@JsonProperty("NMR")
	private  BigDecimal nmr;
	@JsonProperty("CGLD")
	private  BigDecimal cgld;
	@JsonProperty("UMA")
	private  BigDecimal uma;
	@JsonProperty("LRC")
	private  BigDecimal lrc;
	@JsonProperty("YFI")
	private  BigDecimal yfi;
	@JsonProperty("UNI")
	private  BigDecimal uni;
	@JsonProperty("BAL")
	private  BigDecimal bal;
	@JsonProperty("REN")
	private  BigDecimal ren;
	@JsonProperty("WBTC")
	private  BigDecimal wbtc;
	@JsonProperty("NU")
	private  BigDecimal nu;
	@JsonProperty("YFII")
	private  BigDecimal yfii;
	@JsonProperty("FIL")
	private  BigDecimal fil;
	@JsonProperty("AAVE")
	private  BigDecimal aave;
	@JsonProperty("BNT")
	private  BigDecimal bnt;
	@JsonProperty("GRT")
	private  BigDecimal grt;
	@JsonProperty("SNX")
	private  BigDecimal snx;
	@JsonProperty("STORJ")
	private  BigDecimal storj;
	@JsonProperty("SUSHI")
	private  BigDecimal sushi;
	@JsonProperty("MATIC")
	private  BigDecimal matic;
	@JsonProperty("SKL")
	private  BigDecimal skl;
	@JsonProperty("ADA")
	private  BigDecimal ada;
	@JsonProperty("ANKR")
	private  BigDecimal ankr;
	@JsonProperty("CRV")
	private  BigDecimal crv;
	@JsonProperty("ICP")
	private  BigDecimal icp;
	@JsonProperty("NKN")
	private  BigDecimal nkn;
	@JsonProperty("OGN")
	private  BigDecimal ogn;
	private  BigDecimal inch1;
	@JsonProperty("USDT")
	private  BigDecimal usdt;
	@JsonProperty("FORTH")
	private  BigDecimal forth;
	@JsonProperty("CTSI")
	private  BigDecimal ctsi;
	@JsonProperty("TRB")
	private  BigDecimal trb;
	@JsonProperty("POLY")
	private  BigDecimal poly;
	@JsonProperty("MIR")
	private  BigDecimal mir;
	@JsonProperty("RLC")
	private  BigDecimal rlc;
	@JsonProperty("DOT")
	private  BigDecimal dot;
	@JsonProperty("SOL")
	private  BigDecimal sol;
	@JsonProperty("DOGE")
	private  BigDecimal doge;
	@JsonProperty("MLN")
	private  BigDecimal mln;
	@JsonProperty("GTC")
	private  BigDecimal gtc;
	@JsonProperty("AMP")
	private  BigDecimal amp;
	@JsonProperty("SHIB")
	private  BigDecimal shib;
	@JsonProperty("CHZ")
	private  BigDecimal chz;
	@JsonProperty("KEEP")
	private  BigDecimal keep;
	@JsonProperty("LPT")
	private  BigDecimal lpt;
	@JsonProperty("QNT")
	private  BigDecimal qnt;
	@JsonProperty("BOND")
	private  BigDecimal bond;
	@JsonProperty("RLY")
	private  BigDecimal rly;
	@JsonProperty("CLV")
	private  BigDecimal clv;
	@JsonProperty("FARM")
	private  BigDecimal farm;
	@JsonProperty("MASK")
	private  BigDecimal mask;
	@JsonProperty("FET")
	private  BigDecimal fet;
	@JsonProperty("PAX")
	private  BigDecimal pax;
	@JsonProperty("ACH")
	private  BigDecimal ach;
	@JsonProperty("ASM")
	private  BigDecimal asm;
	@JsonProperty("PLA")
	private  BigDecimal pla;
	@JsonProperty("RAI")
	private  BigDecimal rai;
	@JsonProperty("TRIBE")
	private  BigDecimal tribe;
	@JsonProperty("ORN")
	private  BigDecimal orn;
	@JsonProperty("IOTX")
	private  BigDecimal iotx;
	@JsonProperty("UST")
	private  BigDecimal ust;
	@JsonProperty("QUICK")
	private  BigDecimal quick;
	@JsonProperty("AXS")
	private  BigDecimal axs;
	@JsonProperty("REQ")
	private  BigDecimal req;
	@JsonProperty("WLUNA")
	private  BigDecimal wluna;
	@JsonProperty("TRU")
	private  BigDecimal tru;
	@JsonProperty("RAD")
	private  BigDecimal rad;
	@JsonProperty("COTI")
	private  BigDecimal coti;
	@JsonProperty("DDX")
	private  BigDecimal ddx;
	@JsonProperty("SUKU")
	private  BigDecimal suku;
	@JsonProperty("RGT")
	private  BigDecimal rgt;
	@JsonProperty("XYO")
	private  BigDecimal xyo;
	@JsonProperty("ZEN")
	private  BigDecimal zen;
	@JsonProperty("AUCTION")
	private  BigDecimal auction;
	@JsonProperty("BUSD")
	private  BigDecimal busd;
	@JsonProperty("JASMY")
	private  BigDecimal jasmy;
	@JsonProperty("WCFG")
	private  BigDecimal wcfg;
	@JsonProperty("BTRST")
	private  BigDecimal btrst;
	@JsonProperty("AGLD")
	private  BigDecimal agld;
	@JsonProperty("AVAX")
	private  BigDecimal avax;
	@JsonProperty("FX")
	private  BigDecimal fx;
	@JsonProperty("TRAC")
	private  BigDecimal trac;
	@JsonProperty("LCX")
	private  BigDecimal lcx;
	@JsonProperty("ARPA")
	private  BigDecimal arpa;
	@JsonProperty("BADGER")
	private  BigDecimal badger;
	@JsonProperty("KRL")
	private  BigDecimal krl;
	@JsonProperty("PERP")
	private  BigDecimal perp;
	@JsonProperty("RARI")
	private  BigDecimal rari;
	@JsonProperty("DESO")
	private  BigDecimal deso;
	@JsonProperty("API3")
	private  BigDecimal api3;
	@JsonProperty("NCT")
	private  BigDecimal nct;
	@JsonProperty("SHPING")
	private  BigDecimal shping;
	@JsonProperty("UPI")
	private  BigDecimal upi;
	@JsonProperty("CRO")
	private  BigDecimal cro;
	@JsonProperty("AVT")
	private  BigDecimal avt;
	@JsonProperty("MDT")
	private  BigDecimal mdt;
	@JsonProperty("VGX")
	private  BigDecimal vgx;
	@JsonProperty("ALCX")
	private  BigDecimal alcx;
	@JsonProperty("COVAL")
	private  BigDecimal coval;
	@JsonProperty("FOX")
	private  BigDecimal fox;
	@JsonProperty("MUSD")
	private  BigDecimal musd;
	@JsonProperty("GALA")
	private  BigDecimal gala;
	@JsonProperty("POWR")
	private  BigDecimal powr;
	@JsonProperty("GYEN")
	private  BigDecimal gyen;
	@JsonProperty("ALICE")
	private  BigDecimal alice;
	@JsonProperty("INV")
	private  BigDecimal inv;
	@JsonProperty("LQTY")
	private  BigDecimal lqty;
	@JsonProperty("PRO")
	private  BigDecimal pro;
	@JsonProperty("SPELL")
	private  BigDecimal spell;
	@JsonProperty("ENS")
	private  BigDecimal ens;
	@JsonProperty("DIA")
	private  BigDecimal dia;
	@JsonProperty("BLZ")
	private  BigDecimal blz;
	@JsonProperty("CTX")
	private  BigDecimal ctx;
	@JsonProperty("IDEX")
	private  BigDecimal idex;
	@JsonProperty("MCO2")
	private  BigDecimal mco2;
	@JsonProperty("POLS")
	private  BigDecimal pols;
	private  BigDecimal super1;
	@JsonProperty("UNFI")
	private  BigDecimal unfi;
	@JsonProperty("STX")
	private  BigDecimal stx;
	@JsonProperty("GODS")
	private  BigDecimal gods;
	@JsonProperty("IMX")
	private  BigDecimal imx;
	@JsonProperty("RBN")
	private  BigDecimal rbn;
	@JsonProperty("BICO")
	private  BigDecimal bico;
	@JsonProperty("GFI")
	private  BigDecimal gfi;
	@JsonProperty("GLM")
	private  BigDecimal glm;
	@JsonProperty("MPL")
	private  BigDecimal mpl;
	@JsonProperty("PLU")
	private  BigDecimal plu;
	@JsonProperty("FIDA")
	private  BigDecimal fida;
	@JsonProperty("ORCA")
	private  BigDecimal orca;
	@JsonProperty("CRPT")
	private  BigDecimal crpt;
	@JsonProperty("QSP")
	private  BigDecimal qsp;
	@JsonProperty("RNDR")
	private  BigDecimal rndr;
	@JsonProperty("SYN")
	private  BigDecimal syn;
	@JsonProperty("AIOZ")
	private  BigDecimal aioz;
	@JsonProperty("AERGO")
	private  BigDecimal aergo;
	@JsonProperty("HIGH")
	private  BigDecimal high;
	@JsonProperty("ROSE")
	private  BigDecimal rose;
	@JsonProperty("APE")
	private  BigDecimal ape;
	@JsonProperty("MINA")
	private  BigDecimal mina;
	@JsonProperty("GMT")
	private  BigDecimal gmt;
	@JsonProperty("GST")
	private  BigDecimal gst;
	@JsonProperty("GAL")
	private  BigDecimal gal;
	@JsonProperty("DNT")
	private  BigDecimal dnt;
	
	@JsonProperty("SUPER")
	public void getSuper(BigDecimal super1) {
		this.super1 = super1;
	}
	
	@JsonProperty("1INCH")
	public void set1Inch(BigDecimal inch1) {
		this.inch1 = inch1;
	}
	
	public void setAed(BigDecimal aed) {
		this.aed = aed;
	}



	public void setAfn(BigDecimal afn) {
		this.afn = afn;
	}



	public void setAll(BigDecimal all) {
		this.all = all;
	}



	public void setAmd(BigDecimal amd) {
		this.amd = amd;
	}



	public void setAng(BigDecimal ang) {
		this.ang = ang;
	}



	public void setAoa(BigDecimal aoa) {
		this.aoa = aoa;
	}



	public void setArs(BigDecimal ars) {
		this.ars = ars;
	}



	public void setAud(BigDecimal aud) {
		this.aud = aud;
	}



	public void setAwg(BigDecimal awg) {
		this.awg = awg;
	}



	public void setAzn(BigDecimal azn) {
		this.azn = azn;
	}



	public void setBam(BigDecimal bam) {
		this.bam = bam;
	}



	public void setBbd(BigDecimal bbd) {
		this.bbd = bbd;
	}



	public void setBdt(BigDecimal bdt) {
		this.bdt = bdt;
	}



	public void setBgn(BigDecimal bgn) {
		this.bgn = bgn;
	}



	public void setBhd(BigDecimal bhd) {
		this.bhd = bhd;
	}



	public void setBif(BigDecimal bif) {
		this.bif = bif;
	}



	public void setBmd(BigDecimal bmd) {
		this.bmd = bmd;
	}



	public void setBnd(BigDecimal bnd) {
		this.bnd = bnd;
	}



	public void setBob(BigDecimal bob) {
		this.bob = bob;
	}



	public void setBrl(BigDecimal brl) {
		this.brl = brl;
	}



	public void setBsd(BigDecimal bsd) {
		this.bsd = bsd;
	}



	public void setBtc(BigDecimal btc) {
		this.btc = btc;
	}



	public void setBtn(BigDecimal btn) {
		this.btn = btn;
	}



	public void setBwp(BigDecimal bwp) {
		this.bwp = bwp;
	}



	public void setByn(BigDecimal byn) {
		this.byn = byn;
	}



	public void setByr(BigDecimal byr) {
		this.byr = byr;
	}



	public void setBzd(BigDecimal bzd) {
		this.bzd = bzd;
	}



	public void setCad(BigDecimal cad) {
		this.cad = cad;
	}



	public void setCdf(BigDecimal cdf) {
		this.cdf = cdf;
	}



	public void setChf(BigDecimal chf) {
		this.chf = chf;
	}



	public void setClf(BigDecimal clf) {
		this.clf = clf;
	}



	public void setClp(BigDecimal clp) {
		this.clp = clp;
	}



	public void setCny(BigDecimal cny) {
		this.cny = cny;
	}



	public void setCop(BigDecimal cop) {
		this.cop = cop;
	}



	public void setCrc(BigDecimal crc) {
		this.crc = crc;
	}



	public void setCuc(BigDecimal cuc) {
		this.cuc = cuc;
	}



	public void setCve(BigDecimal cve) {
		this.cve = cve;
	}



	public void setCzk(BigDecimal czk) {
		this.czk = czk;
	}



	public void setDjf(BigDecimal djf) {
		this.djf = djf;
	}



	public void setDkk(BigDecimal dkk) {
		this.dkk = dkk;
	}



	public void setDop(BigDecimal dop) {
		this.dop = dop;
	}



	public void setDzd(BigDecimal dzd) {
		this.dzd = dzd;
	}



	public void setEek(BigDecimal eek) {
		this.eek = eek;
	}



	public void setEgp(BigDecimal egp) {
		this.egp = egp;
	}



	public void setErn(BigDecimal ern) {
		this.ern = ern;
	}



	public void setEtb(BigDecimal etb) {
		this.etb = etb;
	}



	public void setEth(BigDecimal eth) {
		this.eth = eth;
	}



	public void setEur(BigDecimal eur) {
		this.eur = eur;
	}



	public void setFjd(BigDecimal fjd) {
		this.fjd = fjd;
	}



	public void setFkp(BigDecimal fkp) {
		this.fkp = fkp;
	}



	public void setGbp(BigDecimal gbp) {
		this.gbp = gbp;
	}



	public void setGel(BigDecimal gel) {
		this.gel = gel;
	}



	public void setGgp(BigDecimal ggp) {
		this.ggp = ggp;
	}



	public void setGhs(BigDecimal ghs) {
		this.ghs = ghs;
	}



	public void setGip(BigDecimal gip) {
		this.gip = gip;
	}



	public void setGmd(BigDecimal gmd) {
		this.gmd = gmd;
	}



	public void setGnf(BigDecimal gnf) {
		this.gnf = gnf;
	}



	public void setGtq(BigDecimal gtq) {
		this.gtq = gtq;
	}



	public void setGyd(BigDecimal gyd) {
		this.gyd = gyd;
	}



	public void setHkd(BigDecimal hkd) {
		this.hkd = hkd;
	}



	public void setHnl(BigDecimal hnl) {
		this.hnl = hnl;
	}



	public void setHrk(BigDecimal hrk) {
		this.hrk = hrk;
	}



	public void setHtg(BigDecimal htg) {
		this.htg = htg;
	}



	public void setHuf(BigDecimal huf) {
		this.huf = huf;
	}



	public void setIdr(BigDecimal idr) {
		this.idr = idr;
	}



	public void setIls(BigDecimal ils) {
		this.ils = ils;
	}



	public void setImp(BigDecimal imp) {
		this.imp = imp;
	}



	public void setInr(BigDecimal inr) {
		this.inr = inr;
	}



	public void setIqd(BigDecimal iqd) {
		this.iqd = iqd;
	}



	public void setIsk(BigDecimal isk) {
		this.isk = isk;
	}



	public void setJep(BigDecimal jep) {
		this.jep = jep;
	}



	public void setJmd(BigDecimal jmd) {
		this.jmd = jmd;
	}



	public void setJod(BigDecimal jod) {
		this.jod = jod;
	}



	public void setJpy(BigDecimal jpy) {
		this.jpy = jpy;
	}



	public void setKes(BigDecimal kes) {
		this.kes = kes;
	}



	public void setKgs(BigDecimal kgs) {
		this.kgs = kgs;
	}



	public void setKhr(BigDecimal khr) {
		this.khr = khr;
	}



	public void setKmf(BigDecimal kmf) {
		this.kmf = kmf;
	}



	public void setKrw(BigDecimal krw) {
		this.krw = krw;
	}



	public void setKwd(BigDecimal kwd) {
		this.kwd = kwd;
	}



	public void setKyd(BigDecimal kyd) {
		this.kyd = kyd;
	}



	public void setKzt(BigDecimal kzt) {
		this.kzt = kzt;
	}



	public void setLak(BigDecimal lak) {
		this.lak = lak;
	}



	public void setLbp(BigDecimal lbp) {
		this.lbp = lbp;
	}



	public void setLkr(BigDecimal lkr) {
		this.lkr = lkr;
	}



	public void setLrd(BigDecimal lrd) {
		this.lrd = lrd;
	}



	public void setLsl(BigDecimal lsl) {
		this.lsl = lsl;
	}



	public void setLtc(BigDecimal ltc) {
		this.ltc = ltc;
	}



	public void setLtl(BigDecimal ltl) {
		this.ltl = ltl;
	}



	public void setLvl(BigDecimal lvl) {
		this.lvl = lvl;
	}



	public void setLyd(BigDecimal lyd) {
		this.lyd = lyd;
	}



	public void setMad(BigDecimal mad) {
		this.mad = mad;
	}



	public void setMdl(BigDecimal mdl) {
		this.mdl = mdl;
	}



	public void setMga(BigDecimal mga) {
		this.mga = mga;
	}



	public void setMkd(BigDecimal mkd) {
		this.mkd = mkd;
	}



	public void setMmk(BigDecimal mmk) {
		this.mmk = mmk;
	}



	public void setMnt(BigDecimal mnt) {
		this.mnt = mnt;
	}



	public void setMop(BigDecimal mop) {
		this.mop = mop;
	}



	public void setMro(BigDecimal mro) {
		this.mro = mro;
	}



	public void setMtl(BigDecimal mtl) {
		this.mtl = mtl;
	}



	public void setMur(BigDecimal mur) {
		this.mur = mur;
	}



	public void setMvr(BigDecimal mvr) {
		this.mvr = mvr;
	}



	public void setMwk(BigDecimal mwk) {
		this.mwk = mwk;
	}



	public void setMxn(BigDecimal mxn) {
		this.mxn = mxn;
	}



	public void setMyr(BigDecimal myr) {
		this.myr = myr;
	}



	public void setMzn(BigDecimal mzn) {
		this.mzn = mzn;
	}



	public void setNad(BigDecimal nad) {
		this.nad = nad;
	}



	public void setNgn(BigDecimal ngn) {
		this.ngn = ngn;
	}



	public void setNio(BigDecimal nio) {
		this.nio = nio;
	}



	public void setNok(BigDecimal nok) {
		this.nok = nok;
	}



	public void setNpr(BigDecimal npr) {
		this.npr = npr;
	}



	public void setNzd(BigDecimal nzd) {
		this.nzd = nzd;
	}



	public void setOmr(BigDecimal omr) {
		this.omr = omr;
	}



	public void setPab(BigDecimal pab) {
		this.pab = pab;
	}



	public void setPen(BigDecimal pen) {
		this.pen = pen;
	}



	public void setPgk(BigDecimal pgk) {
		this.pgk = pgk;
	}



	public void setPhp(BigDecimal php) {
		this.php = php;
	}



	public void setPkr(BigDecimal pkr) {
		this.pkr = pkr;
	}



	public void setPln(BigDecimal pln) {
		this.pln = pln;
	}



	public void setPyg(BigDecimal pyg) {
		this.pyg = pyg;
	}



	public void setQar(BigDecimal qar) {
		this.qar = qar;
	}



	public void setRon(BigDecimal ron) {
		this.ron = ron;
	}



	public void setRsd(BigDecimal rsd) {
		this.rsd = rsd;
	}



	public void setRub(BigDecimal rub) {
		this.rub = rub;
	}



	public void setRwf(BigDecimal rwf) {
		this.rwf = rwf;
	}



	public void setSar(BigDecimal sar) {
		this.sar = sar;
	}



	public void setSbd(BigDecimal sbd) {
		this.sbd = sbd;
	}



	public void setScr(BigDecimal scr) {
		this.scr = scr;
	}



	public void setSek(BigDecimal sek) {
		this.sek = sek;
	}



	public void setSgd(BigDecimal sgd) {
		this.sgd = sgd;
	}



	public void setShp(BigDecimal shp) {
		this.shp = shp;
	}



	public void setSll(BigDecimal sll) {
		this.sll = sll;
	}



	public void setSos(BigDecimal sos) {
		this.sos = sos;
	}



	public void setSrd(BigDecimal srd) {
		this.srd = srd;
	}



	public void setSsp(BigDecimal ssp) {
		this.ssp = ssp;
	}



	public void setStd(BigDecimal std) {
		this.std = std;
	}



	public void setSvc(BigDecimal svc) {
		this.svc = svc;
	}



	public void setSzl(BigDecimal szl) {
		this.szl = szl;
	}



	public void setThb(BigDecimal thb) {
		this.thb = thb;
	}



	public void setTjs(BigDecimal tjs) {
		this.tjs = tjs;
	}



	public void setTmt(BigDecimal tmt) {
		this.tmt = tmt;
	}



	public void setTnd(BigDecimal tnd) {
		this.tnd = tnd;
	}



	public void setTop(BigDecimal top) {
		this.top = top;
	}


	@JsonProperty("TRY")
	public void setTry(BigDecimal try1) {
		this.try1 = try1;
	}



	public void setTtd(BigDecimal ttd) {
		this.ttd = ttd;
	}



	public void setTwd(BigDecimal twd) {
		this.twd = twd;
	}



	public void setTzs(BigDecimal tzs) {
		this.tzs = tzs;
	}



	public void setUah(BigDecimal uah) {
		this.uah = uah;
	}



	public void setUgx(BigDecimal ugx) {
		this.ugx = ugx;
	}



	public void setUsd(BigDecimal usd) {
		this.usd = usd;
	}



	public void setUyu(BigDecimal uyu) {
		this.uyu = uyu;
	}



	public void setUzs(BigDecimal uzs) {
		this.uzs = uzs;
	}



	public void setVef(BigDecimal vef) {
		this.vef = vef;
	}



	public void setVnd(BigDecimal vnd) {
		this.vnd = vnd;
	}



	public void setVuv(BigDecimal vuv) {
		this.vuv = vuv;
	}



	public void setWst(BigDecimal wst) {
		this.wst = wst;
	}



	public void setXaf(BigDecimal xaf) {
		this.xaf = xaf;
	}



	public void setXag(BigDecimal xag) {
		this.xag = xag;
	}



	public void setXau(BigDecimal xau) {
		this.xau = xau;
	}



	public void setXcd(BigDecimal xcd) {
		this.xcd = xcd;
	}



	public void setXdr(BigDecimal xdr) {
		this.xdr = xdr;
	}



	public void setXof(BigDecimal xof) {
		this.xof = xof;
	}



	public void setXpd(BigDecimal xpd) {
		this.xpd = xpd;
	}



	public void setXpf(BigDecimal xpf) {
		this.xpf = xpf;
	}



	public void setXpt(BigDecimal xpt) {
		this.xpt = xpt;
	}



	public void setYer(BigDecimal yer) {
		this.yer = yer;
	}



	public void setZar(BigDecimal zar) {
		this.zar = zar;
	}



	public void setZmk(BigDecimal zmk) {
		this.zmk = zmk;
	}



	public void setZmw(BigDecimal zmw) {
		this.zmw = zmw;
	}



	public void setZwl(BigDecimal zwl) {
		this.zwl = zwl;
	}



	public void setVes(BigDecimal ves) {
		this.ves = ves;
	}



	public void setXba(BigDecimal xba) {
		this.xba = xba;
	}



	public void setXts(BigDecimal xts) {
		this.xts = xts;
	}



	public void setGbx(BigDecimal gbx) {
		this.gbx = gbx;
	}



	public void setCnh(BigDecimal cnh) {
		this.cnh = cnh;
	}



	public void setZwd(BigDecimal zwd) {
		this.zwd = zwd;
	}



	public void setBch(BigDecimal bch) {
		this.bch = bch;
	}



	public void setBsv(BigDecimal bsv) {
		this.bsv = bsv;
	}



	public void setEth2(BigDecimal eth2) {
		this.eth2 = eth2;
	}



	public void setEtc(BigDecimal etc) {
		this.etc = etc;
	}



	public void setZrx(BigDecimal zrx) {
		this.zrx = zrx;
	}



	public void setUsdc(BigDecimal usdc) {
		this.usdc = usdc;
	}



	public void setBat(BigDecimal bat) {
		this.bat = bat;
	}



	public void setLoom(BigDecimal loom) {
		this.loom = loom;
	}



	public void setMana(BigDecimal mana) {
		this.mana = mana;
	}



	public void setKnc(BigDecimal knc) {
		this.knc = knc;
	}



	public void setLink(BigDecimal link) {
		this.link = link;
	}



	public void setMkr(BigDecimal mkr) {
		this.mkr = mkr;
	}



	public void setCvc(BigDecimal cvc) {
		this.cvc = cvc;
	}



	public void setOmg(BigDecimal omg) {
		this.omg = omg;
	}



	public void setGnt(BigDecimal gnt) {
		this.gnt = gnt;
	}



	public void setDai(BigDecimal dai) {
		this.dai = dai;
	}



	public void setSnt(BigDecimal snt) {
		this.snt = snt;
	}



	public void setZec(BigDecimal zec) {
		this.zec = zec;
	}



	public void setXrp(BigDecimal xrp) {
		this.xrp = xrp;
	}



	public void setRep(BigDecimal rep) {
		this.rep = rep;
	}



	public void setXlm(BigDecimal xlm) {
		this.xlm = xlm;
	}



	public void setEos(BigDecimal eos) {
		this.eos = eos;
	}



	public void setXtz(BigDecimal xtz) {
		this.xtz = xtz;
	}



	public void setAlgo(BigDecimal algo) {
		this.algo = algo;
	}



	public void setDash(BigDecimal dash) {
		this.dash = dash;
	}



	public void setAtom(BigDecimal atom) {
		this.atom = atom;
	}



	public void setOxt(BigDecimal oxt) {
		this.oxt = oxt;
	}



	public void setComp(BigDecimal comp) {
		this.comp = comp;
	}



	public void setEnj(BigDecimal enj) {
		this.enj = enj;
	}



	public void setRepv2(BigDecimal repv2) {
		this.repv2 = repv2;
	}



	public void setBand(BigDecimal band) {
		this.band = band;
	}



	public void setNmr(BigDecimal nmr) {
		this.nmr = nmr;
	}



	public void setCgld(BigDecimal cgld) {
		this.cgld = cgld;
	}



	public void setUma(BigDecimal uma) {
		this.uma = uma;
	}



	public void setLrc(BigDecimal lrc) {
		this.lrc = lrc;
	}



	public void setYfi(BigDecimal yfi) {
		this.yfi = yfi;
	}



	public void setUni(BigDecimal uni) {
		this.uni = uni;
	}



	public void setBal(BigDecimal bal) {
		this.bal = bal;
	}



	public void setRen(BigDecimal ren) {
		this.ren = ren;
	}



	public void setWbtc(BigDecimal wbtc) {
		this.wbtc = wbtc;
	}



	public void setNu(BigDecimal nu) {
		this.nu = nu;
	}



	public void setYfii(BigDecimal yfii) {
		this.yfii = yfii;
	}



	public void setFil(BigDecimal fil) {
		this.fil = fil;
	}



	public void setAave(BigDecimal aave) {
		this.aave = aave;
	}



	public void setBnt(BigDecimal bnt) {
		this.bnt = bnt;
	}



	public void setGrt(BigDecimal grt) {
		this.grt = grt;
	}



	public void setSnx(BigDecimal snx) {
		this.snx = snx;
	}



	public void setStorj(BigDecimal storj) {
		this.storj = storj;
	}



	public void setSushi(BigDecimal sushi) {
		this.sushi = sushi;
	}



	public void setMatic(BigDecimal matic) {
		this.matic = matic;
	}



	public void setSkl(BigDecimal skl) {
		this.skl = skl;
	}



	public void setAda(BigDecimal ada) {
		this.ada = ada;
	}



	public void setAnkr(BigDecimal ankr) {
		this.ankr = ankr;
	}



	public void setCrv(BigDecimal crv) {
		this.crv = crv;
	}



	public void setIcp(BigDecimal icp) {
		this.icp = icp;
	}



	public void setNkn(BigDecimal nkn) {
		this.nkn = nkn;
	}



	public void setOgn(BigDecimal ogn) {
		this.ogn = ogn;
	}



	public void setUsdt(BigDecimal usdt) {
		this.usdt = usdt;
	}



	public void setForth(BigDecimal forth) {
		this.forth = forth;
	}



	public void setCtsi(BigDecimal ctsi) {
		this.ctsi = ctsi;
	}



	public void setTrb(BigDecimal trb) {
		this.trb = trb;
	}



	public void setPoly(BigDecimal poly) {
		this.poly = poly;
	}



	public void setMir(BigDecimal mir) {
		this.mir = mir;
	}



	public void setRlc(BigDecimal rlc) {
		this.rlc = rlc;
	}



	public void setDot(BigDecimal dot) {
		this.dot = dot;
	}



	public void setSol(BigDecimal sol) {
		this.sol = sol;
	}



	public void setDoge(BigDecimal doge) {
		this.doge = doge;
	}



	public void setMln(BigDecimal mln) {
		this.mln = mln;
	}



	public void setGtc(BigDecimal gtc) {
		this.gtc = gtc;
	}



	public void setAmp(BigDecimal amp) {
		this.amp = amp;
	}



	public void setShib(BigDecimal shib) {
		this.shib = shib;
	}



	public void setChz(BigDecimal chz) {
		this.chz = chz;
	}



	public void setKeep(BigDecimal keep) {
		this.keep = keep;
	}



	public void setLpt(BigDecimal lpt) {
		this.lpt = lpt;
	}



	public void setQnt(BigDecimal qnt) {
		this.qnt = qnt;
	}



	public void setBond(BigDecimal bond) {
		this.bond = bond;
	}



	public void setRly(BigDecimal rly) {
		this.rly = rly;
	}



	public void setClv(BigDecimal clv) {
		this.clv = clv;
	}



	public void setFarm(BigDecimal farm) {
		this.farm = farm;
	}



	public void setMask(BigDecimal mask) {
		this.mask = mask;
	}



	public void setFet(BigDecimal fet) {
		this.fet = fet;
	}



	public void setPax(BigDecimal pax) {
		this.pax = pax;
	}



	public void setAch(BigDecimal ach) {
		this.ach = ach;
	}



	public void setAsm(BigDecimal asm) {
		this.asm = asm;
	}



	public void setPla(BigDecimal pla) {
		this.pla = pla;
	}



	public void setRai(BigDecimal rai) {
		this.rai = rai;
	}



	public void setTribe(BigDecimal tribe) {
		this.tribe = tribe;
	}



	public void setOrn(BigDecimal orn) {
		this.orn = orn;
	}



	public void setIotx(BigDecimal iotx) {
		this.iotx = iotx;
	}



	public void setUst(BigDecimal ust) {
		this.ust = ust;
	}



	public void setQuick(BigDecimal quick) {
		this.quick = quick;
	}



	public void setAxs(BigDecimal axs) {
		this.axs = axs;
	}



	public void setReq(BigDecimal req) {
		this.req = req;
	}



	public void setWluna(BigDecimal wluna) {
		this.wluna = wluna;
	}



	public void setTru(BigDecimal tru) {
		this.tru = tru;
	}



	public void setRad(BigDecimal rad) {
		this.rad = rad;
	}



	public void setCoti(BigDecimal coti) {
		this.coti = coti;
	}



	public void setDdx(BigDecimal ddx) {
		this.ddx = ddx;
	}



	public void setSuku(BigDecimal suku) {
		this.suku = suku;
	}



	public void setRgt(BigDecimal rgt) {
		this.rgt = rgt;
	}



	public void setXyo(BigDecimal xyo) {
		this.xyo = xyo;
	}



	public void setZen(BigDecimal zen) {
		this.zen = zen;
	}



	public void setAuction(BigDecimal auction) {
		this.auction = auction;
	}



	public void setBusd(BigDecimal busd) {
		this.busd = busd;
	}



	public void setJasmy(BigDecimal jasmy) {
		this.jasmy = jasmy;
	}



	public void setWcfg(BigDecimal wcfg) {
		this.wcfg = wcfg;
	}



	public void setBtrst(BigDecimal btrst) {
		this.btrst = btrst;
	}



	public void setAgld(BigDecimal agld) {
		this.agld = agld;
	}



	public void setAvax(BigDecimal avax) {
		this.avax = avax;
	}



	public void setFx(BigDecimal fx) {
		this.fx = fx;
	}



	public void setTrac(BigDecimal trac) {
		this.trac = trac;
	}



	public void setLcx(BigDecimal lcx) {
		this.lcx = lcx;
	}



	public void setArpa(BigDecimal arpa) {
		this.arpa = arpa;
	}



	public void setBadger(BigDecimal badger) {
		this.badger = badger;
	}



	public void setKrl(BigDecimal krl) {
		this.krl = krl;
	}



	public void setPerp(BigDecimal perp) {
		this.perp = perp;
	}



	public void setRari(BigDecimal rari) {
		this.rari = rari;
	}



	public void setDeso(BigDecimal deso) {
		this.deso = deso;
	}



	public void setApi3(BigDecimal api3) {
		this.api3 = api3;
	}



	public void setNct(BigDecimal nct) {
		this.nct = nct;
	}



	public void setShping(BigDecimal shping) {
		this.shping = shping;
	}



	public void setUpi(BigDecimal upi) {
		this.upi = upi;
	}



	public void setCro(BigDecimal cro) {
		this.cro = cro;
	}



	public void setAvt(BigDecimal avt) {
		this.avt = avt;
	}



	public void setMdt(BigDecimal mdt) {
		this.mdt = mdt;
	}



	public void setVgx(BigDecimal vgx) {
		this.vgx = vgx;
	}



	public void setAlcx(BigDecimal alcx) {
		this.alcx = alcx;
	}



	public void setCoval(BigDecimal coval) {
		this.coval = coval;
	}



	public void setFox(BigDecimal fox) {
		this.fox = fox;
	}



	public void setMusd(BigDecimal musd) {
		this.musd = musd;
	}



	public void setGala(BigDecimal gala) {
		this.gala = gala;
	}



	public void setPowr(BigDecimal powr) {
		this.powr = powr;
	}



	public void setGyen(BigDecimal gyen) {
		this.gyen = gyen;
	}



	public void setAlice(BigDecimal alice) {
		this.alice = alice;
	}



	public void setInv(BigDecimal inv) {
		this.inv = inv;
	}



	public void setLqty(BigDecimal lqty) {
		this.lqty = lqty;
	}



	public void setPro(BigDecimal pro) {
		this.pro = pro;
	}



	public void setSpell(BigDecimal spell) {
		this.spell = spell;
	}



	public void setEns(BigDecimal ens) {
		this.ens = ens;
	}



	public void setDia(BigDecimal dia) {
		this.dia = dia;
	}



	public void setBlz(BigDecimal blz) {
		this.blz = blz;
	}



	public void setCtx(BigDecimal ctx) {
		this.ctx = ctx;
	}



	public void setIdex(BigDecimal idex) {
		this.idex = idex;
	}



	public void setMco2(BigDecimal mco2) {
		this.mco2 = mco2;
	}



	public void setPols(BigDecimal pols) {
		this.pols = pols;
	}



	public void setUnfi(BigDecimal unfi) {
		this.unfi = unfi;
	}



	public void setStx(BigDecimal stx) {
		this.stx = stx;
	}



	public void setGods(BigDecimal gods) {
		this.gods = gods;
	}



	public void setImx(BigDecimal imx) {
		this.imx = imx;
	}



	public void setRbn(BigDecimal rbn) {
		this.rbn = rbn;
	}



	public void setBico(BigDecimal bico) {
		this.bico = bico;
	}



	public void setGfi(BigDecimal gfi) {
		this.gfi = gfi;
	}



	public void setGlm(BigDecimal glm) {
		this.glm = glm;
	}



	public void setMpl(BigDecimal mpl) {
		this.mpl = mpl;
	}



	public void setPlu(BigDecimal plu) {
		this.plu = plu;
	}



	public void setFida(BigDecimal fida) {
		this.fida = fida;
	}



	public void setOrca(BigDecimal orca) {
		this.orca = orca;
	}



	public void setCrpt(BigDecimal crpt) {
		this.crpt = crpt;
	}



	public void setQsp(BigDecimal qsp) {
		this.qsp = qsp;
	}



	public void setRndr(BigDecimal rndr) {
		this.rndr = rndr;
	}



	public void setSyn(BigDecimal syn) {
		this.syn = syn;
	}



	public void setAioz(BigDecimal aioz) {
		this.aioz = aioz;
	}



	public void setAergo(BigDecimal aergo) {
		this.aergo = aergo;
	}



	public void setHigh(BigDecimal high) {
		this.high = high;
	}



	public void setRose(BigDecimal rose) {
		this.rose = rose;
	}



	public void setApe(BigDecimal ape) {
		this.ape = ape;
	}



	public void setMina(BigDecimal mina) {
		this.mina = mina;
	}



	public void setGmt(BigDecimal gmt) {
		this.gmt = gmt;
	}



	public void setGst(BigDecimal gst) {
		this.gst = gst;
	}



	public void setGal(BigDecimal gal) {
		this.gal = gal;
	}



	public BigDecimal getUsdt() {
		return usdt;
	}

	public BigDecimal getForth() {
		return forth;
	}

	public BigDecimal getCtsi() {
		return ctsi;
	}

	public BigDecimal getTrb() {
		return trb;
	}

	public BigDecimal getPoly() {
		return poly;
	}

	public BigDecimal getMir() {
		return mir;
	}

	public BigDecimal getRlc() {
		return rlc;
	}

	public BigDecimal getDot() {
		return dot;
	}

	public BigDecimal getSol() {
		return sol;
	}

	public BigDecimal getDoge() {
		return doge;
	}

	public BigDecimal getMln() {
		return mln;
	}

	public BigDecimal getGtc() {
		return gtc;
	}

	public BigDecimal getAmp() {
		return amp;
	}

	public BigDecimal getShib() {
		return shib;
	}

	public BigDecimal getChz() {
		return chz;
	}

	public BigDecimal getKeep() {
		return keep;
	}

	public BigDecimal getLpt() {
		return lpt;
	}

	public BigDecimal getQnt() {
		return qnt;
	}

	public BigDecimal getBond() {
		return bond;
	}

	public BigDecimal getRly() {
		return rly;
	}

	public BigDecimal getClv() {
		return clv;
	}

	public BigDecimal getFarm() {
		return farm;
	}

	public BigDecimal getMask() {
		return mask;
	}

	public BigDecimal getFet() {
		return fet;
	}

	public BigDecimal getPax() {
		return pax;
	}

	public BigDecimal getAch() {
		return ach;
	}

	public BigDecimal getAsm() {
		return asm;
	}

	public BigDecimal getPla() {
		return pla;
	}

	public BigDecimal getRai() {
		return rai;
	}

	public BigDecimal getTribe() {
		return tribe;
	}

	public BigDecimal getOrn() {
		return orn;
	}

	public BigDecimal getIotx() {
		return iotx;
	}

	public BigDecimal getUst() {
		return ust;
	}

	public BigDecimal getQuick() {
		return quick;
	}

	public BigDecimal getAxs() {
		return axs;
	}

	public BigDecimal getReq() {
		return req;
	}

	public BigDecimal getWluna() {
		return wluna;
	}

	public BigDecimal getTru() {
		return tru;
	}

	public BigDecimal getRad() {
		return rad;
	}

	public BigDecimal getCoti() {
		return coti;
	}

	public BigDecimal getDdx() {
		return ddx;
	}

	public BigDecimal getSuku() {
		return suku;
	}

	public BigDecimal getRgt() {
		return rgt;
	}

	public BigDecimal getXyo() {
		return xyo;
	}

	public BigDecimal getZen() {
		return zen;
	}

	public BigDecimal getAuction() {
		return auction;
	}

	public BigDecimal getBusd() {
		return busd;
	}

	public BigDecimal getJasmy() {
		return jasmy;
	}

	public BigDecimal getWcfg() {
		return wcfg;
	}

	public BigDecimal getBtrst() {
		return btrst;
	}

	public BigDecimal getAgld() {
		return agld;
	}

	public BigDecimal getAvax() {
		return avax;
	}

	public BigDecimal getFx() {
		return fx;
	}

	public BigDecimal getTrac() {
		return trac;
	}

	public BigDecimal getLcx() {
		return lcx;
	}

	public BigDecimal getArpa() {
		return arpa;
	}

	public BigDecimal getBadger() {
		return badger;
	}

	public BigDecimal getKrl() {
		return krl;
	}

	public BigDecimal getPerp() {
		return perp;
	}

	public BigDecimal getRari() {
		return rari;
	}

	public BigDecimal getDeso() {
		return deso;
	}

	public BigDecimal getApi3() {
		return api3;
	}

	public BigDecimal getNct() {
		return nct;
	}

	public BigDecimal getShping() {
		return shping;
	}

	public BigDecimal getUpi() {
		return upi;
	}

	public BigDecimal getCro() {
		return cro;
	}

	public BigDecimal getAvt() {
		return avt;
	}

	public BigDecimal getMdt() {
		return mdt;
	}

	public BigDecimal getVgx() {
		return vgx;
	}

	public BigDecimal getAlcx() {
		return alcx;
	}

	public BigDecimal getCoval() {
		return coval;
	}

	public BigDecimal getFox() {
		return fox;
	}

	public BigDecimal getMusd() {
		return musd;
	}

	public BigDecimal getGala() {
		return gala;
	}

	public BigDecimal getPowr() {
		return powr;
	}

	public BigDecimal getGyen() {
		return gyen;
	}

	public BigDecimal getAlice() {
		return alice;
	}

	public BigDecimal getInv() {
		return inv;
	}

	public BigDecimal getLqty() {
		return lqty;
	}

	public BigDecimal getPro() {
		return pro;
	}

	public BigDecimal getSpell() {
		return spell;
	}

	public BigDecimal getEns() {
		return ens;
	}

	public BigDecimal getDia() {
		return dia;
	}

	public BigDecimal getBlz() {
		return blz;
	}

	public BigDecimal getCtx() {
		return ctx;
	}

	public BigDecimal getIdex() {
		return idex;
	}

	public BigDecimal getMco2() {
		return mco2;
	}

	public BigDecimal getPols() {
		return pols;
	}

	public BigDecimal getUnfi() {
		return unfi;
	}

	public BigDecimal getStx() {
		return stx;
	}

	public BigDecimal getGods() {
		return gods;
	}

	public BigDecimal getImx() {
		return imx;
	}

	public BigDecimal getRbn() {
		return rbn;
	}

	public BigDecimal getBico() {
		return bico;
	}

	public BigDecimal getGfi() {
		return gfi;
	}

	public BigDecimal getGlm() {
		return glm;
	}

	public BigDecimal getMpl() {
		return mpl;
	}

	public BigDecimal getPlu() {
		return plu;
	}

	public BigDecimal getFida() {
		return fida;
	}

	public BigDecimal getOrca() {
		return orca;
	}

	public BigDecimal getCrpt() {
		return crpt;
	}

	public BigDecimal getQsp() {
		return qsp;
	}

	public BigDecimal getRndr() {
		return rndr;
	}

	public BigDecimal getSyn() {
		return syn;
	}

	public BigDecimal getAioz() {
		return aioz;
	}

	public BigDecimal getAergo() {
		return aergo;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public BigDecimal getRose() {
		return rose;
	}

	public BigDecimal getApe() {
		return ape;
	}

	public BigDecimal getMina() {
		return mina;
	}

	public BigDecimal getGmt() {
		return gmt;
	}

	public BigDecimal getGst() {
		return gst;
	}

	public BigDecimal getGal() {
		return gal;
	}

	public BigDecimal getSuper() {
		return super1;
	}

	public BigDecimal getMana() {
		return mana;
	}

	public BigDecimal getKnc() {
		return knc;
	}

	public BigDecimal getLink() {
		return link;
	}

	public BigDecimal getMkr() {
		return mkr;
	}

	public BigDecimal getCvc() {
		return cvc;
	}

	public BigDecimal getOmg() {
		return omg;
	}

	public BigDecimal getGnt() {
		return gnt;
	}

	public BigDecimal getDai() {
		return dai;
	}

	public BigDecimal getSnt() {
		return snt;
	}

	public BigDecimal getZec() {
		return zec;
	}

	public BigDecimal getXrp() {
		return xrp;
	}

	public BigDecimal getRep() {
		return rep;
	}

	public BigDecimal getXlm() {
		return xlm;
	}

	public BigDecimal getEos() {
		return eos;
	}

	public BigDecimal getXtz() {
		return xtz;
	}

	public BigDecimal getAlgo() {
		return algo;
	}

	public BigDecimal getDash() {
		return dash;
	}

	public BigDecimal getAtom() {
		return atom;
	}

	public BigDecimal getOxt() {
		return oxt;
	}

	public BigDecimal getComp() {
		return comp;
	}

	public BigDecimal getEnj() {
		return enj;
	}

	public BigDecimal getRepv2() {
		return repv2;
	}

	public BigDecimal getBand() {
		return band;
	}

	public BigDecimal getNmr() {
		return nmr;
	}

	public BigDecimal getCgld() {
		return cgld;
	}

	public BigDecimal getUma() {
		return uma;
	}

	public BigDecimal getLrc() {
		return lrc;
	}

	public BigDecimal getYfi() {
		return yfi;
	}

	public BigDecimal getUni() {
		return uni;
	}

	public BigDecimal getBal() {
		return bal;
	}

	public BigDecimal getRen() {
		return ren;
	}

	public BigDecimal getWbtc() {
		return wbtc;
	}

	public BigDecimal getNu() {
		return nu;
	}

	public BigDecimal getYfii() {
		return yfii;
	}

	public BigDecimal getFil() {
		return fil;
	}

	public BigDecimal getAave() {
		return aave;
	}

	public BigDecimal getBnt() {
		return bnt;
	}

	public BigDecimal getGrt() {
		return grt;
	}

	public BigDecimal getSnx() {
		return snx;
	}

	public BigDecimal getStorj() {
		return storj;
	}

	public BigDecimal getSushi() {
		return sushi;
	}

	public BigDecimal getMatic() {
		return matic;
	}

	public BigDecimal getSkl() {
		return skl;
	}

	public BigDecimal getAda() {
		return ada;
	}

	public BigDecimal getAnkr() {
		return ankr;
	}

	public BigDecimal getCrv() {
		return crv;
	}

	public BigDecimal getIcp() {
		return icp;
	}

	public BigDecimal getNkn() {
		return nkn;
	}

	public BigDecimal getOgn() {
		return ogn;
	}

	public BigDecimal get1inch() {
		return this.inch1;
	}

	public BigDecimal getLoom() {
		return loom;
	}

	public BigDecimal getBat() {
		return bat;
	}

	public BigDecimal getUsdc() {
		return usdc;
	}

	public BigDecimal getZrx() {
		return zrx;
	}

	public BigDecimal getEtc() {
		return etc;
	}

	public BigDecimal getEth2() {
		return eth2;
	}

	public BigDecimal getBsv() {
		return bsv;
	}

	public BigDecimal getBch() {
		return bch;
	}

	public BigDecimal getCnh() {
		return cnh;
	}

	public BigDecimal getZwd() {
		return zwd;
	}

	public BigDecimal getGbx() {
		return gbx;
	}

	public BigDecimal getXts() {
		return xts;
	}

	public BigDecimal getVes() {
		return ves;
	}

	public BigDecimal getXba() {
		return xba;
	}

	public BigDecimal getAed() {
		return aed;
	}

	public BigDecimal getAfn() {
		return afn;
	}

	public BigDecimal getAll() {
		return all;
	}

	public BigDecimal getAmd() {
		return amd;
	}

	public BigDecimal getAng() {
		return ang;
	}

	public BigDecimal getAoa() {
		return aoa;
	}

	public BigDecimal getArs() {
		return ars;
	}

	public BigDecimal getAud() {
		return aud;
	}

	public BigDecimal getAwg() {
		return awg;
	}

	public BigDecimal getAzn() {
		return azn;
	}

	public BigDecimal getBam() {
		return bam;
	}

	public BigDecimal getBbd() {
		return bbd;
	}

	public BigDecimal getBdt() {
		return bdt;
	}

	public BigDecimal getBgn() {
		return bgn;
	}

	public BigDecimal getBhd() {
		return bhd;
	}

	public BigDecimal getBif() {
		return bif;
	}

	public BigDecimal getBmd() {
		return bmd;
	}

	public BigDecimal getBnd() {
		return bnd;
	}

	public BigDecimal getBob() {
		return bob;
	}

	public BigDecimal getBrl() {
		return brl;
	}

	public BigDecimal getBsd() {
		return bsd;
	}

	public BigDecimal getBtc() {
		return btc;
	}

	public BigDecimal getBtn() {
		return btn;
	}

	public BigDecimal getBwp() {
		return bwp;
	}

	public BigDecimal getByn() {
		return byn;
	}

	public BigDecimal getByr() {
		return byr;
	}

	public BigDecimal getBzd() {
		return bzd;
	}

	public BigDecimal getCad() {
		return cad;
	}

	public BigDecimal getCdf() {
		return cdf;
	}

	public BigDecimal getChf() {
		return chf;
	}

	public BigDecimal getClf() {
		return clf;
	}

	public BigDecimal getClp() {
		return clp;
	}

	public BigDecimal getCny() {
		return cny;
	}

	public BigDecimal getCop() {
		return cop;
	}

	public BigDecimal getCrc() {
		return crc;
	}

	public BigDecimal getCuc() {
		return cuc;
	}

	public BigDecimal getCve() {
		return cve;
	}

	public BigDecimal getCzk() {
		return czk;
	}

	public BigDecimal getDjf() {
		return djf;
	}

	public BigDecimal getDkk() {
		return dkk;
	}

	public BigDecimal getDop() {
		return dop;
	}

	public BigDecimal getDzd() {
		return dzd;
	}

	public BigDecimal getEek() {
		return eek;
	}

	public BigDecimal getEgp() {
		return egp;
	}

	public BigDecimal getErn() {
		return ern;
	}

	public BigDecimal getEtb() {
		return etb;
	}

	public BigDecimal getEth() {
		return eth;
	}

	public BigDecimal getEur() {
		return eur;
	}

	public BigDecimal getFjd() {
		return fjd;
	}

	public BigDecimal getFkp() {
		return fkp;
	}

	public BigDecimal getGbp() {
		return gbp;
	}

	public BigDecimal getGel() {
		return gel;
	}

	public BigDecimal getGgp() {
		return ggp;
	}

	public BigDecimal getGhs() {
		return ghs;
	}

	public BigDecimal getGip() {
		return gip;
	}

	public BigDecimal getGmd() {
		return gmd;
	}

	public BigDecimal getGnf() {
		return gnf;
	}

	public BigDecimal getGtq() {
		return gtq;
	}

	public BigDecimal getGyd() {
		return gyd;
	}

	public BigDecimal getHkd() {
		return hkd;
	}

	public BigDecimal getHnl() {
		return hnl;
	}

	public BigDecimal getHrk() {
		return hrk;
	}

	public BigDecimal getHtg() {
		return htg;
	}

	public BigDecimal getHuf() {
		return huf;
	}

	public BigDecimal getIdr() {
		return idr;
	}

	public BigDecimal getIls() {
		return ils;
	}

	public BigDecimal getImp() {
		return imp;
	}

	public BigDecimal getInr() {
		return inr;
	}

	public BigDecimal getIqd() {
		return iqd;
	}

	public BigDecimal getIsk() {
		return isk;
	}

	public BigDecimal getJep() {
		return jep;
	}

	public BigDecimal getJmd() {
		return jmd;
	}

	public BigDecimal getJod() {
		return jod;
	}

	public BigDecimal getJpy() {
		return jpy;
	}

	public BigDecimal getKes() {
		return kes;
	}

	public BigDecimal getKgs() {
		return kgs;
	}

	public BigDecimal getKhr() {
		return khr;
	}

	public BigDecimal getKmf() {
		return kmf;
	}

	public BigDecimal getKrw() {
		return krw;
	}

	public BigDecimal getKwd() {
		return kwd;
	}

	public BigDecimal getKyd() {
		return kyd;
	}

	public BigDecimal getKzt() {
		return kzt;
	}

	public BigDecimal getLak() {
		return lak;
	}

	public BigDecimal getLbp() {
		return lbp;
	}

	public BigDecimal getLkr() {
		return lkr;
	}

	public BigDecimal getLrd() {
		return lrd;
	}

	public BigDecimal getLsl() {
		return lsl;
	}

	public BigDecimal getLtc() {
		return ltc;
	}

	public BigDecimal getLtl() {
		return ltl;
	}

	public BigDecimal getLvl() {
		return lvl;
	}

	public BigDecimal getLyd() {
		return lyd;
	}

	public BigDecimal getMad() {
		return mad;
	}

	public BigDecimal getMdl() {
		return mdl;
	}

	public BigDecimal getMga() {
		return mga;
	}

	public BigDecimal getMkd() {
		return mkd;
	}

	public BigDecimal getMmk() {
		return mmk;
	}

	public BigDecimal getMnt() {
		return mnt;
	}

	public BigDecimal getMop() {
		return mop;
	}

	public BigDecimal getMro() {
		return mro;
	}

	public BigDecimal getMtl() {
		return mtl;
	}

	public BigDecimal getMur() {
		return mur;
	}

	public BigDecimal getMvr() {
		return mvr;
	}

	public BigDecimal getMwk() {
		return mwk;
	}

	public BigDecimal getMxn() {
		return mxn;
	}

	public BigDecimal getMyr() {
		return myr;
	}

	public BigDecimal getMzn() {
		return mzn;
	}

	public BigDecimal getNad() {
		return nad;
	}

	public BigDecimal getNgn() {
		return ngn;
	}

	public BigDecimal getNio() {
		return nio;
	}

	public BigDecimal getNok() {
		return nok;
	}

	public BigDecimal getNpr() {
		return npr;
	}

	public BigDecimal getNzd() {
		return nzd;
	}

	public BigDecimal getOmr() {
		return omr;
	}

	public BigDecimal getPab() {
		return pab;
	}

	public BigDecimal getPen() {
		return pen;
	}

	public BigDecimal getPgk() {
		return pgk;
	}

	public BigDecimal getPhp() {
		return php;
	}

	public BigDecimal getPkr() {
		return pkr;
	}

	public BigDecimal getPln() {
		return pln;
	}

	public BigDecimal getPyg() {
		return pyg;
	}

	public BigDecimal getQar() {
		return qar;
	}

	public BigDecimal getRon() {
		return ron;
	}

	public BigDecimal getRsd() {
		return rsd;
	}

	public BigDecimal getRub() {
		return rub;
	}

	public BigDecimal getRwf() {
		return rwf;
	}

	public BigDecimal getSar() {
		return sar;
	}

	public BigDecimal getSbd() {
		return sbd;
	}

	public BigDecimal getScr() {
		return scr;
	}

	public BigDecimal getSek() {
		return sek;
	}

	public BigDecimal getSgd() {
		return sgd;
	}

	public BigDecimal getShp() {
		return shp;
	}

	public BigDecimal getSll() {
		return sll;
	}

	public BigDecimal getSos() {
		return sos;
	}

	public BigDecimal getSrd() {
		return srd;
	}

	public BigDecimal getSsp() {
		return ssp;
	}

	public BigDecimal getStd() {
		return std;
	}

	public BigDecimal getSvc() {
		return svc;
	}

	public BigDecimal getSzl() {
		return szl;
	}

	public BigDecimal getThb() {
		return thb;
	}

	public BigDecimal getTjs() {
		return tjs;
	}

	public BigDecimal getTmt() {
		return tmt;
	}

	public BigDecimal getTnd() {
		return tnd;
	}

	public BigDecimal getTop() {
		return top;
	}

	public BigDecimal getTry1() {
		return try1;
	}

	public BigDecimal getTtd() {
		return ttd;
	}

	public BigDecimal getTwd() {
		return twd;
	}

	public BigDecimal getTzs() {
		return tzs;
	}

	public BigDecimal getUah() {
		return uah;
	}

	public BigDecimal getUgx() {
		return ugx;
	}

	public BigDecimal getUsd() {
		return usd;
	}

	public BigDecimal getUyu() {
		return uyu;
	}

	public BigDecimal getUzs() {
		return uzs;
	}

	public BigDecimal getVef() {
		return vef;
	}

	public BigDecimal getVnd() {
		return vnd;
	}

	public BigDecimal getVuv() {
		return vuv;
	}

	public BigDecimal getWst() {
		return wst;
	}

	public BigDecimal getXaf() {
		return xaf;
	}

	public BigDecimal getXag() {
		return xag;
	}

	public BigDecimal getXau() {
		return xau;
	}

	public BigDecimal getXcd() {
		return xcd;
	}

	public BigDecimal getXdr() {
		return xdr;
	}

	public BigDecimal getXof() {
		return xof;
	}

	public BigDecimal getXpd() {
		return xpd;
	}

	public BigDecimal getXpf() {
		return xpf;
	}

	public BigDecimal getXpt() {
		return xpt;
	}

	public BigDecimal getYer() {
		return yer;
	}

	public BigDecimal getZar() {
		return zar;
	}

	public BigDecimal getZmk() {
		return zmk;
	}

	public BigDecimal getZmw() {
		return zmw;
	}

	public BigDecimal getZwl() {
		return zwl;
	}

	@Override
	public String toString() {
		return "QuoteCb [aed=" + aed + ", afn=" + afn + ", all=" + all + ", amd=" + amd + ", ang=" + ang + ", aoa="
				+ aoa + ", ars=" + ars + ", aud=" + aud + ", awg=" + awg + ", azn=" + azn + ", bam=" + bam + ", bbd="
				+ bbd + ", bdt=" + bdt + ", bgn=" + bgn + ", bhd=" + bhd + ", bif=" + bif + ", bmd=" + bmd + ", bnd="
				+ bnd + ", bob=" + bob + ", brl=" + brl + ", bsd=" + bsd + ", btc=" + btc + ", btn=" + btn + ", bwp="
				+ bwp + ", byn=" + byn + ", byr=" + byr + ", bzd=" + bzd + ", cad=" + cad + ", cdf=" + cdf + ", chf="
				+ chf + ", clf=" + clf + ", clp=" + clp + ", cny=" + cny + ", cop=" + cop + ", crc=" + crc + ", cuc="
				+ cuc + ", cve=" + cve + ", czk=" + czk + ", djf=" + djf + ", dkk=" + dkk + ", dop=" + dop + ", dzd="
				+ dzd + ", eek=" + eek + ", egp=" + egp + ", ern=" + ern + ", etb=" + etb + ", eth=" + eth + ", eur="
				+ eur + ", fjd=" + fjd + ", fkd=" + fkp + ", gbp=" + gbp + ", gel=" + gel + ", ggp=" + ggp + ", ghs="
				+ ghs + ", gip=" + gip + ", gmd=" + gmd + ", gnf=" + gnf + ", gtq=" + gtq + ", gyd=" + gyd + ", hkd="
				+ hkd + ", hnl=" + hnl + ", hrk=" + hrk + ", htg=" + htg + ", huf=" + huf + ", idr=" + idr + ", ils="
				+ ils + ", imp=" + imp + ", inr=" + inr + ", iqd=" + iqd + ", isk=" + isk + ", jep=" + jep + ", jmd="
				+ jmd + ", jod=" + jod + ", jpy=" + jpy + ", kes=" + kes + ", kgs=" + kgs + ", khr=" + khr + ", kmf="
				+ kmf + ", krw=" + krw + ", kwd=" + kwd + ", kyd=" + kyd + ", kzt=" + kzt + ", lak=" + lak + ", lbp="
				+ lbp + ", lkr=" + lkr + ", ldr=" + lrd + ", lsl=" + lsl + ", ltc=" + ltc + ", ltl=" + ltl + ", lvl="
				+ lvl + ", lyd=" + lyd + ", mad=" + mad + ", mdl=" + mdl + ", mga=" + mga + ", mkd=" + mkd + ", mmk="
				+ mmk + ", mnt=" + mnt + ", mop=" + mop + ", mro=" + mro + ", mtl=" + mtl + ", mur=" + mur + ", mvr="
				+ mvr + ", mwk=" + mwk + ", mxn=" + mxn + ", myr=" + myr + ", mzn=" + mzn + ", nad=" + nad + ", ngn="
				+ ngn + ", nio=" + nio + ", nok=" + nok + ", npr=" + npr + ", nzd=" + nzd + ", omr=" + omr + ", pab="
				+ pab + ", pen=" + pen + ", pgk=" + pgk + ", php=" + php + ", pkr=" + pkr + ", pln=" + pln + ", pyg="
				+ pyg + ", qar=" + qar + ", ron=" + ron + ", rsd=" + rsd + ", rub=" + rub + ", rwf=" + rwf + ", sar="
				+ sar + ", sbd=" + sbd + ", scr=" + scr + ", sek=" + sek + ", sgd=" + sgd + ", shp=" + shp + ", sll="
				+ sll + ", sos=" + sos + ", srd=" + srd + ", ssp=" + ssp + ", std=" + std + ", svc=" + svc + ", szl="
				+ szl + ", thb=" + thb + ", tjs=" + tjs + ", tmt=" + tmt + ", tnd=" + tnd + ", top=" + top + ", try1="
				+ try1 + ", tdd=" + ttd + ", twd=" + twd + ", tzs=" + tzs + ", uah=" + uah + ", ugx=" + ugx + ", usd="
				+ usd + ", uyu=" + uyu + ", uzs=" + uzs + ", vef=" + vef + ", vnd=" + vnd + ", vuv=" + vuv + ", wst="
				+ wst + ", xaf=" + xaf + ", xag=" + xag + ", xau=" + xau + ", xcd=" + xcd + ", xdr=" + xdr + ", xof="
				+ xof + ", xpd=" + xpd + ", xpf=" + xpf + ", xpt=" + xpt + ", yer=" + yer + ", zar=" + zar + ", zmk="
				+ zmk + ", zmw=" + zmw + ", zml=" + zwl + "]";
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
