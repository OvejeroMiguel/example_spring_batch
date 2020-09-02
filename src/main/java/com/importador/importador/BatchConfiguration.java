package com.importador.importador;

import com.importador.importador.listener.JobListener;
import com.importador.importador.model.Persona;
import com.importador.importador.processor.PersonaItemProcesor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Persona> reader() {
        return new FlatFileItemReaderBuilder<Persona>()
                .name("personaItemReader")
                .resource(new ClassPathResource("sample-data.csv"))
                .delimited()
                .names(new String[]{"primerNombre", "segundoNombre", "telefono"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Persona.class);
                }})
                .build();
    }

    @Bean
    public PersonaItemProcesor procesor() {
        return new PersonaItemProcesor();
    }

    @Bean
    public JdbcBatchItemWriter<Persona> writer(DataSource dataSource){
        return new JdbcBatchItemWriterBuilder<Persona>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO PERSONA (primer_nombre, segundo_nombre, telefono) VALUES (:primerNombre, :segundoNombre, :telefono)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Job importPersonaJob(JobListener jobListener, Step step1){
        return jobBuilderFactory.get("importPersonaJob")
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Persona> writer){
        return stepBuilderFactory.get("step1")
                .<Persona, Persona> chunk(10)
                .reader(reader())
                .processor(procesor())
                .writer(writer)
                .build();
    }

}
