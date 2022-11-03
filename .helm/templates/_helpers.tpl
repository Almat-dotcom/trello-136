{{/*
Return the proper Keycloak image name
*/}}
{{- define "keycloak.image" -}}
{{ include "common.images.image" (dict "imageRoot" .Values.image "global" .Values.global) }}
{{- end -}}
{{/*
Return true if a configmap object should be created
*/}}
{{- define "keycloak.createConfigmap" -}}
{{- if and .Values.configuration (not .Values.existingConfigmap) }}
    {{- true -}}
{{- end -}}
{{- end -}}

{{/*
Return admin password in base64 if it is present in .Values
*/}}
{{- define "keycloak.auth.password" -}}
{{- if .Values.auth.adminPassword -}}
    {{- .Values.auth.adminPassword | b64enc -}}
{{- end -}}
{{- end -}}

{{/*
Return database password in base64 if it is present in .Values
*/}}
{{- define "keycloak.database.password" -}}
{{- if .Values.database.password -}}
    {{- .Values.database.password | b64enc -}}
{{- end -}}
{{- end -}}

{{/*
Return the proper Docker Image Registry Secret Names
*/}}
{{- define "keycloak.imagePullSecrets" -}}
{{- include "common.images.pullSecrets" (dict "images" (list .Values.image) "global" .Values.global) -}}
{{- end -}}

{{/*
Return the secret containing the Keycloak admin password
*/}}
{{- define "keycloak.secretName" -}}
{{- $secretName := .Values.auth.existingSecret -}}
{{- if $secretName -}}
    {{- printf "%s" (tpl $secretName $) -}}
{{- else -}}
    {{- printf "%s" (include "common.names.fullname" .) | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{/*
Return the secret key that contains the Keycloak admin password
*/}}
{{- define "keycloak.secretKey" -}}
{{- $secretName := .Values.auth.existingSecret -}}
{{- if and $secretName .Values.auth.passwordSecretKey -}}
    {{- printf "%s" .Values.auth.passwordSecretKey -}}
{{- else -}}
    {{- print "admin-password" -}}
{{- end -}}
{{- end -}}

{{/*
Return the Keycloak configuration configmap
*/}}
{{- define "keycloak.configmapName" -}}
{{- if .Values.existingConfigmap -}}
    {{- printf "%s" (tpl .Values.existingConfigmap $) -}}
{{- else -}}
    {{- printf "%s-configuration" (include "common.names.fullname" .) -}}
{{- end -}}
{{- end -}}
