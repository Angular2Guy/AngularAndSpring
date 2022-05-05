{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "helm-chart.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "helm-chart.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "helm-chart.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

Create envApp values
*/}}
{{- define "helpers.list-envApp-variables"}}
{{- $secretName := .Values.secret.nameApp -}}
{{- range $key, $val := .Values.envApp.secret }}
- name: {{ $key }}
  valueFrom:
    secretKeyRef:
      name: {{ $secretName }}
      key: {{ $key }}
{{- end}}
{{- range $key, $val := .Values.envApp.normal }}
- name: {{ $key }}
  value: {{ $val | quote }}
{{- end}}
{{- end }}

Create envDb values
*/}}
{{- define "helpers.list-envDb-variables"}}
{{- $secretName := .Values.secret.nameDb -}}
{{- range $key, $val := .Values.envDb.secret }}
- name: {{ $key }}
  valueFrom:
    secretKeyRef:
      name: {{ $secretName }}
      key: {{ $key }}
{{- end}}
{{- range $key, $val := .Values.envDb.normal }}
- name: {{ $key }}
  value: {{ $val | quote }}
{{- end}}
{{- end }}