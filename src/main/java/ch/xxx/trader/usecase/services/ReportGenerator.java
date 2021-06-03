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
package ch.xxx.trader.usecase.services;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.xxx.trader.domain.model.QuotePdf;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReportGenerator {
	private static final Logger log = LoggerFactory.getLogger(ReportGenerator.class);	
	
	public Mono<byte[]> generateReport( Flux<QuotePdf> quotes) {
		byte[] result = new byte[0];
		Date start = new Date();
		try {
			JasperReport jasperReport = JasperCompileManager.compileReport(this.getClass().getClassLoader().getResourceAsStream("currencyReport.jrxml"));
			Map<String,Object> params = new HashMap<>();
			List<QuotePdf> myQuotes = quotes.collectList().block();
			params.put("quotes", new JRBeanCollectionDataSource(myQuotes));
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,params, new JRBeanCollectionDataSource(myQuotes));
			
			JRPdfExporter pdfExporter = new JRPdfExporter();
            pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            ByteArrayOutputStream pdfReportStream = new ByteArrayOutputStream();
            pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfReportStream));
            pdfExporter.exportReport();
            
            result = pdfReportStream.toByteArray();
		} catch (JRException e) {
			log.error("Report generation failed.",e);
		}
		log.info("Report generated in: "+ (new Date().getTime() - start.getTime()) +"ms");
		return Mono.just(result);
	}
}
