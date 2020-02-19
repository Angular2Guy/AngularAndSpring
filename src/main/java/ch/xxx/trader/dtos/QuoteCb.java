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
package ch.xxx.trader.dtos;

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

	private final BigDecimal aed;
	private final BigDecimal afn;
	private final BigDecimal all;
	private final BigDecimal amd;
	private final BigDecimal ang;
	private final BigDecimal aoa;
	private final BigDecimal ars;
	private final BigDecimal aud;
	private final BigDecimal awg;
	private final BigDecimal azn;
	private final BigDecimal bam;
	private final BigDecimal bbd;
	private final BigDecimal bdt;
	private final BigDecimal bgn;
	private final BigDecimal bhd;
	private final BigDecimal bif;
	private final BigDecimal bmd;
	private final BigDecimal bnd;
	private final BigDecimal bob;
	private final BigDecimal brl;
	private final BigDecimal bsd;
	private final BigDecimal btc;
	private final BigDecimal btn;
	private final BigDecimal bwp;
	private final BigDecimal byn;
	private final BigDecimal byr;
	private final BigDecimal bzd;
	private final BigDecimal cad;
	private final BigDecimal cdf;
	private final BigDecimal chf;
	private final BigDecimal clf;
	private final BigDecimal clp;
	private final BigDecimal cny;
	private final BigDecimal cop;
	private final BigDecimal crc;
	private final BigDecimal cuc;
	private final BigDecimal cve;
	private final BigDecimal czk;
	private final BigDecimal djf;
	private final BigDecimal dkk;
	private final BigDecimal dop;
	private final BigDecimal dzd;
	private final BigDecimal eek;
	private final BigDecimal egp;
	private final BigDecimal ern;
	private final BigDecimal etb;
	private final BigDecimal eth;
	private final BigDecimal eur;
	private final BigDecimal fjd;
	private final BigDecimal fkp;
	private final BigDecimal gbp;
	private final BigDecimal gel;
	private final BigDecimal ggp;
	private final BigDecimal ghs;
	private final BigDecimal gip;
	private final BigDecimal gmd;
	private final BigDecimal gnf;
	private final BigDecimal gtq;
	private final BigDecimal gyd;
	private final BigDecimal hkd;
	private final BigDecimal hnl;	
	private final BigDecimal hrk;
	private final BigDecimal htg;
	private final BigDecimal huf;
	private final BigDecimal idr;
	private final BigDecimal ils;
	private final BigDecimal imp;
	private final BigDecimal inr;
	private final BigDecimal iqd;
	private final BigDecimal isk;
	private final BigDecimal jep;
	private final BigDecimal jmd;
	private final BigDecimal jod;
	private final BigDecimal jpy;
	private final BigDecimal kes;
	private final BigDecimal kgs;
	private final BigDecimal khr;
	private final BigDecimal kmf;
	private final BigDecimal krw;
	private final BigDecimal kwd;
	private final BigDecimal kyd;
	private final BigDecimal kzt;
	private final BigDecimal lak;
	private final BigDecimal lbp;
	private final BigDecimal lkr;
	private final BigDecimal lrd;
	private final BigDecimal lsl;
	private final BigDecimal ltc;
	private final BigDecimal ltl;
	private final BigDecimal lvl;
	private final BigDecimal lyd;
	private final BigDecimal mad;
	private final BigDecimal mdl;
	private final BigDecimal mga;
	private final BigDecimal mkd;
	private final BigDecimal mmk;
	private final BigDecimal mnt;
	private final BigDecimal mop;
	private final BigDecimal mro;
	private final BigDecimal mtl;
	private final BigDecimal mur;
	private final BigDecimal mvr;
	private final BigDecimal mwk;
	private final BigDecimal mxn;
	private final BigDecimal myr;
	private final BigDecimal mzn;
	private final BigDecimal nad;
	private final BigDecimal ngn;
	private final BigDecimal nio;
	private final BigDecimal nok;
	private final BigDecimal npr;
	private final BigDecimal nzd;
	private final BigDecimal omr;
	private final BigDecimal pab;
	private final BigDecimal pen;
	private final BigDecimal pgk;
	private final BigDecimal php;
	private final BigDecimal pkr;
	private final BigDecimal pln;
	private final BigDecimal pyg;
	private final BigDecimal qar;
	private final BigDecimal ron;
	private final BigDecimal rsd;
	private final BigDecimal rub;
	private final BigDecimal rwf;
	private final BigDecimal sar;
	private final BigDecimal sbd;
	private final BigDecimal scr;
	private final BigDecimal sek;
	private final BigDecimal sgd;
	private final BigDecimal shp;
	private final BigDecimal sll;
	private final BigDecimal sos;
	private final BigDecimal srd;
	private final BigDecimal ssp;
	private final BigDecimal std;
	private final BigDecimal svc;
	private final BigDecimal szl;
	private final BigDecimal thb;
	private final BigDecimal tjs;
	private final BigDecimal tmt;
	private final BigDecimal tnd;
	private final BigDecimal top;
	private final BigDecimal try1;
	private final BigDecimal ttd;			
	private final BigDecimal twd;
	private final BigDecimal tzs;
	private final BigDecimal uah;
	private final BigDecimal ugx;
	private final BigDecimal usd;
	private final BigDecimal uyu;
	private final BigDecimal uzs;
	private final BigDecimal vef;
	private final BigDecimal vnd;
	private final BigDecimal vuv;
	private final BigDecimal wst;
	private final BigDecimal xaf;
	private final BigDecimal xag;
	private final BigDecimal xau;
	private final BigDecimal xcd;
	private final BigDecimal xdr;
	private final BigDecimal xof;
	private final BigDecimal xpd;
	private final BigDecimal xpf;
	private final BigDecimal xpt;
	private final BigDecimal yer;
	private final BigDecimal zar;
	private final BigDecimal zmk;
	private final BigDecimal zmw;
	private final BigDecimal zwl;
	
	
	
	public QuoteCb(@JsonProperty("AED") BigDecimal aed, @JsonProperty("AFN")BigDecimal afn, @JsonProperty("ALL") BigDecimal all, @JsonProperty("AMD") BigDecimal amd, @JsonProperty("ANG") BigDecimal ang, @JsonProperty("AOA") BigDecimal aoa,
			@JsonProperty("ARS") BigDecimal ars, @JsonProperty("AUD")BigDecimal aud, @JsonProperty("AWG") BigDecimal awg, @JsonProperty("AZN") BigDecimal azn,@JsonProperty("BAM") BigDecimal bam, @JsonProperty("BBD") BigDecimal bbd,
			@JsonProperty("BDT")BigDecimal bdt, @JsonProperty("BGN") BigDecimal bgn,@JsonProperty("BHD") BigDecimal bhd,@JsonProperty("BIF") BigDecimal bif, @JsonProperty("BMD") BigDecimal bmd,@JsonProperty("BND") BigDecimal bnd,
			@JsonProperty("BOB") BigDecimal bob, @JsonProperty("BRL")BigDecimal brl, @JsonProperty("BSD")BigDecimal bsd, @JsonProperty("BTC")BigDecimal btc, @JsonProperty("BTN") BigDecimal btn,@JsonProperty("BWP") BigDecimal bwp,
			@JsonProperty("BYN")BigDecimal byn,@JsonProperty("BYR") BigDecimal byr, @JsonProperty("BZD")BigDecimal bzd, @JsonProperty("CAD")BigDecimal cad,@JsonProperty("CDF") BigDecimal cdf,@JsonProperty("CHF") BigDecimal chf,
			@JsonProperty("CLF")BigDecimal clf,@JsonProperty("CLP") BigDecimal clp,@JsonProperty("CNY") BigDecimal cny, @JsonProperty("COP")BigDecimal cop, @JsonProperty("CRC")BigDecimal crc, @JsonProperty("CUC")BigDecimal cuc,
			@JsonProperty("CVE")BigDecimal cve,@JsonProperty("CZK") BigDecimal czk,@JsonProperty("DJF") BigDecimal djf,@JsonProperty("DKK") BigDecimal dkk,@JsonProperty("DOP") BigDecimal dop, @JsonProperty("DZD")BigDecimal dzd,
			@JsonProperty("EEK")BigDecimal eek,@JsonProperty("EGP") BigDecimal egp,@JsonProperty("ERN") BigDecimal ern,@JsonProperty("ETB") BigDecimal etb,@JsonProperty("ETH") BigDecimal eth, @JsonProperty("EUR")BigDecimal eur,
			@JsonProperty("FJD")BigDecimal fjd,@JsonProperty("FKP") BigDecimal fkp,@JsonProperty("GBP") BigDecimal gbp, @JsonProperty("GEL")BigDecimal gel,@JsonProperty("GGP") BigDecimal ggp, @JsonProperty("GHS")BigDecimal ghs,
			@JsonProperty("GIP")BigDecimal gip,@JsonProperty("GMD") BigDecimal gmd,@JsonProperty("GNF") BigDecimal gnf,@JsonProperty("GTQ") BigDecimal gtq, @JsonProperty("GYD")BigDecimal gyd, @JsonProperty("HKD")BigDecimal hkd,
			@JsonProperty("HNL")BigDecimal hnl,@JsonProperty("HRK") BigDecimal hrk,@JsonProperty("HTG") BigDecimal htg,@JsonProperty("HUF") BigDecimal huf,@JsonProperty("IDR") BigDecimal idr, @JsonProperty("ILS")BigDecimal ils,
			@JsonProperty("IMP")BigDecimal imp,@JsonProperty("INR") BigDecimal inr,@JsonProperty("IQD") BigDecimal iqd,@JsonProperty("ISK") BigDecimal isk,@JsonProperty("JEP") BigDecimal jep, @JsonProperty("JMD")BigDecimal jmd,
			@JsonProperty("JOD")BigDecimal jod, @JsonProperty("JPY")BigDecimal jpy, @JsonProperty("KES")BigDecimal kes,@JsonProperty("KGS") BigDecimal kgs,@JsonProperty("KHR") BigDecimal khr, @JsonProperty("KMF")BigDecimal kmf,
			@JsonProperty("KRW")BigDecimal krw,@JsonProperty("KWD") BigDecimal kwd, @JsonProperty("KYD")BigDecimal kyd,@JsonProperty("KZT") BigDecimal kzt,@JsonProperty("LAK") BigDecimal lak, @JsonProperty("LBP")BigDecimal lbp,
			@JsonProperty("LKR")BigDecimal lkr,@JsonProperty("LRD") BigDecimal lrd, @JsonProperty("LSL")BigDecimal lsl,@JsonProperty("LTC") BigDecimal ltc, @JsonProperty("LTL")BigDecimal ltl, @JsonProperty("LVL")BigDecimal lvl,
			@JsonProperty("LYD")BigDecimal lyd,@JsonProperty("MAD") BigDecimal mad, @JsonProperty("MDL")BigDecimal mdl, @JsonProperty("MGA")BigDecimal mga, @JsonProperty("MKD")BigDecimal mkd,@JsonProperty("MMK") BigDecimal mmk,
			@JsonProperty("MNT")BigDecimal mnt,@JsonProperty("MOP") BigDecimal mop,@JsonProperty("MRO") BigDecimal mro,@JsonProperty("MTL") BigDecimal mtl, @JsonProperty("MUR")BigDecimal mur, @JsonProperty("MVR")BigDecimal mvr,
			@JsonProperty("MWK")BigDecimal mwk,@JsonProperty("MXN") BigDecimal mxn,@JsonProperty("MYR") BigDecimal myr,@JsonProperty("MZN") BigDecimal mzn, @JsonProperty("NAD")BigDecimal nad, @JsonProperty("NGN")BigDecimal ngn,
			@JsonProperty("NIO")BigDecimal nio,@JsonProperty("NOK") BigDecimal nok,@JsonProperty("NPR") BigDecimal npr, @JsonProperty("NZD")BigDecimal nzd, @JsonProperty("OMR")BigDecimal omr, @JsonProperty("PAB")BigDecimal pab,
			@JsonProperty("PEN")BigDecimal pen, @JsonProperty("PGK")BigDecimal pgk, @JsonProperty("PHP")BigDecimal php, @JsonProperty("PKR")BigDecimal pkr, @JsonProperty("PLN")BigDecimal pln, @JsonProperty("PYG")BigDecimal pyg,
			@JsonProperty("QAR")BigDecimal qar, @JsonProperty("RON")BigDecimal ron,@JsonProperty("RSD") BigDecimal rsd, @JsonProperty("RUB")BigDecimal rub, @JsonProperty("RWF")BigDecimal rwf, @JsonProperty("SAR")BigDecimal sar,
			@JsonProperty("SBD")BigDecimal sbd, @JsonProperty("SCR")BigDecimal scr,@JsonProperty("SEK") BigDecimal sek, @JsonProperty("SGD")BigDecimal sgd, @JsonProperty("SHP")BigDecimal shp, @JsonProperty("SLL")BigDecimal sll,
			@JsonProperty("SOS")BigDecimal sos, @JsonProperty("SRD")BigDecimal srd, @JsonProperty("SSP")BigDecimal ssp, @JsonProperty("STD")BigDecimal std, @JsonProperty("SVC")BigDecimal svc,@JsonProperty("SZL") BigDecimal szl,
			@JsonProperty("THB")BigDecimal thb,@JsonProperty("TJS") BigDecimal tjs,@JsonProperty("TMT") BigDecimal tmt, @JsonProperty("TND")BigDecimal tnd, @JsonProperty("TOP")BigDecimal top, @JsonProperty("TRY")BigDecimal try1,
			@JsonProperty("TTD")BigDecimal ttd,@JsonProperty("TWD") BigDecimal twd,@JsonProperty("TZS") BigDecimal tzs, @JsonProperty("UAH")BigDecimal uah,@JsonProperty("UGX") BigDecimal ugx, @JsonProperty("USD")BigDecimal usd,
			@JsonProperty("UYU")BigDecimal uyu,@JsonProperty("UZS") BigDecimal uzs,@JsonProperty("VEF") BigDecimal vef,@JsonProperty("VND") BigDecimal vnd,@JsonProperty("VUV") BigDecimal vuv, @JsonProperty("WST")BigDecimal wst,
			@JsonProperty("XAF")BigDecimal xaf,@JsonProperty("XAG") BigDecimal xag, @JsonProperty("XAU")BigDecimal xau,@JsonProperty("XCD") BigDecimal xcd, @JsonProperty("XDR")BigDecimal xdr, @JsonProperty("XOF")BigDecimal xof,
			@JsonProperty("XPD")BigDecimal xpd, @JsonProperty("XPF")BigDecimal xpf,@JsonProperty("XPT") BigDecimal xpt,@JsonProperty("YER") BigDecimal yer, @JsonProperty("ZAR")BigDecimal zar, @JsonProperty("ZMK")BigDecimal zmk,
			@JsonProperty("ZMW")BigDecimal zmw, @JsonProperty("ZWL")BigDecimal zwl) {
		super();
		this.aed = aed;
		this.afn = afn;
		this.all = all;
		this.amd = amd;
		this.ang = ang;
		this.aoa = aoa;
		this.ars = ars;
		this.aud = aud;
		this.awg = awg;
		this.azn = azn;
		this.bam = bam;
		this.bbd = bbd;
		this.bdt = bdt;
		this.bgn = bgn;
		this.bhd = bhd;
		this.bif = bif;
		this.bmd = bmd;
		this.bnd = bnd;
		this.bob = bob;
		this.brl = brl;
		this.bsd = bsd;
		this.btc = btc;
		this.btn = btn;
		this.bwp = bwp;
		this.byn = byn;
		this.byr = byr;
		this.bzd = bzd;
		this.cad = cad;
		this.cdf = cdf;
		this.chf = chf;
		this.clf = clf;
		this.clp = clp;
		this.cny = cny;
		this.cop = cop;
		this.crc = crc;
		this.cuc = cuc;
		this.cve = cve;
		this.czk = czk;
		this.djf = djf;
		this.dkk = dkk;
		this.dop = dop;
		this.dzd = dzd;
		this.eek = eek;
		this.egp = egp;
		this.ern = ern;
		this.etb = etb;
		this.eth = eth;
		this.eur = eur;
		this.fjd = fjd;
		this.fkp = fkp;
		this.gbp = gbp;
		this.gel = gel;
		this.ggp = ggp;
		this.ghs = ghs;
		this.gip = gip;
		this.gmd = gmd;
		this.gnf = gnf;
		this.gtq = gtq;
		this.gyd = gyd;
		this.hkd = hkd;
		this.hnl = hnl;
		this.hrk = hrk;
		this.htg = htg;
		this.huf = huf;
		this.idr = idr;
		this.ils = ils;
		this.imp = imp;
		this.inr = inr;
		this.iqd = iqd;
		this.isk = isk;
		this.jep = jep;
		this.jmd = jmd;
		this.jod = jod;
		this.jpy = jpy;
		this.kes = kes;
		this.kgs = kgs;
		this.khr = khr;
		this.kmf = kmf;
		this.krw = krw;
		this.kwd = kwd;
		this.kyd = kyd;
		this.kzt = kzt;
		this.lak = lak;
		this.lbp = lbp;
		this.lkr = lkr;
		this.lrd = lrd;
		this.lsl = lsl;
		this.ltc = ltc;
		this.ltl = ltl;
		this.lvl = lvl;
		this.lyd = lyd;
		this.mad = mad;
		this.mdl = mdl;
		this.mga = mga;
		this.mkd = mkd;
		this.mmk = mmk;
		this.mnt = mnt;
		this.mop = mop;
		this.mro = mro;
		this.mtl = mtl;
		this.mur = mur;
		this.mvr = mvr;
		this.mwk = mwk;
		this.mxn = mxn;
		this.myr = myr;
		this.mzn = mzn;
		this.nad = nad;
		this.ngn = ngn;
		this.nio = nio;
		this.nok = nok;
		this.npr = npr;
		this.nzd = nzd;
		this.omr = omr;
		this.pab = pab;
		this.pen = pen;
		this.pgk = pgk;
		this.php = php;
		this.pkr = pkr;
		this.pln = pln;
		this.pyg = pyg;
		this.qar = qar;
		this.ron = ron;
		this.rsd = rsd;
		this.rub = rub;
		this.rwf = rwf;
		this.sar = sar;
		this.sbd = sbd;
		this.scr = scr;
		this.sek = sek;
		this.sgd = sgd;
		this.shp = shp;
		this.sll = sll;
		this.sos = sos;
		this.srd = srd;
		this.ssp = ssp;
		this.std = std;
		this.svc = svc;
		this.szl = szl;
		this.thb = thb;
		this.tjs = tjs;
		this.tmt = tmt;
		this.tnd = tnd;
		this.top = top;
		this.try1 = try1;
		this.ttd = ttd;
		this.twd = twd;
		this.tzs = tzs;
		this.uah = uah;
		this.ugx = ugx;
		this.usd = usd;
		this.uyu = uyu;
		this.uzs = uzs;
		this.vef = vef;
		this.vnd = vnd;
		this.vuv = vuv;
		this.wst = wst;
		this.xaf = xaf;
		this.xag = xag;
		this.xau = xau;
		this.xcd = xcd;
		this.xdr = xdr;
		this.xof = xof;
		this.xpd = xpd;
		this.xpf = xpf;
		this.xpt = xpt;
		this.yer = yer;
		this.zar = zar;
		this.zmk = zmk;
		this.zmw = zmw;
		this.zwl = zwl;
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
