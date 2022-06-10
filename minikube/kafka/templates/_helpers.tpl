{{/*
Create envZookeeper values
*/}}
{{- define "helpers.list-envZookeeperApp-variables"}}
{{- $secretName := .Values.secret.nameZookeeper -}}
{{- range $key, $val := .Values.envZookeeper.secret }}
- name: {{ $key }}
  valueFrom:
    secretKeyRef:
      name: {{ $secretName }}
      key: {{ $key }}
{{- end}}
{{- range $key, $val := .Values.envZookeeper.normal }}
- name: {{ $key }}
  value: {{ $val | quote }}
{{- end}}
{{- end }}

{{/*
Create envKafka values
*/}}
{{- define "helpers.list-envKafkaApp-variables"}}
{{- $secretName := .Values.secret.nameKafka -}}
{{- range $key, $val := .Values.envKafka.secret }}
- name: {{ $key }}
  valueFrom:
    secretKeyRef:
      name: {{ $secretName }}
      key: {{ $key }}
{{- end}}
{{- range $key, $val := .Values.envKafka.normal }}
- name: {{ $key }}
  value: {{ $val | quote }}
{{- end}}
{{- end }}