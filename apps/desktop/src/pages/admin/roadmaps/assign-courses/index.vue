<script setup>
definePageMeta({
  middleware: 'auth'
});

const router = useRouter();
onMounted(() => {
  router.push('/admin/roadmaps');
});
</script>